package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.RowState;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.extend.TableDetailAdapter;
import ru.megazlo.apnea.service.AlertService;
import ru.megazlo.apnea.service.ApneaService;

@EFragment(R.layout.table_detail)
public class TableDetailFragment extends Fragment implements FabClickListener {

    private TableApnea tableApnea;

    @StringRes(R.string.total_time)
    String totalTimeStr;

    @Bean
    ApneaService apneaService;
    @Bean
    AlertService alertService;

    @ViewById(R.id.arc_progress)
    ArcProgress prg;
    @ViewById(R.id.total_time)
    TextView totalTime;
    @ViewById(R.id.list_row)
    ListView listView;

    private Timer timer;

    private List<TableApneaRow> rows;

    @AfterViews
    void init() {
        rows = apneaService.getRowsForTable(tableApnea);
        final TableDetailAdapter adapter = new TableDetailAdapter(getActivity(), R.layout.table_detail_row);
        adapter.addAll(rows);
        listView.setAdapter(adapter);
        updateTotalTime();
    }

    private void updateTotalTime() {
        prg.setProgress(0);
        if (rows != null && rows.size() > 0) {
            prg.setMax(rows.get(0).getBreathe());
            int total = 0;
            for (TableApneaRow r : rows) {
                total += r.getBreathe() + r.getHold();
            }
            totalTime.setText(String.format(totalTimeStr, Utils.formatMS(total)));
        }
    }

    public void setTableApnea(TableApnea tableApnea) {
        this.tableApnea = tableApnea;
    }

    private TimerPref getNextIntervalAndUpdate() {
        if (rows == null || rows.size() == 0) {
            return null;
        }
        for (int i = 0; i < rows.size(); i++) {
            final TableApneaRow r = rows.get(i);
            if (r.getState() == RowState.BREATHE) {//если дышали возвращаем задержку
                r.setState(RowState.HOLD);
                return new TimerPref(r.getHold(), RowState.HOLD);
            } else if (r.getState() == RowState.HOLD) {
                r.setState(RowState.NONE);// если была задержка, то сбрасываем и если есть еще возвращаем дыхание
                if (i != rows.size() - 1) {
                    rows.get(i + 1).setState(RowState.BREATHE);
                    return new TimerPref(rows.get(i + 1).getBreathe(), RowState.BREATHE);
                }
                return null;
            }
        }
        // если ничего нет начинаем с дыхания
        rows.get(0).setState(RowState.BREATHE);
        return new TimerPref(rows.get(0).getBreathe(), RowState.BREATHE);
    }

    /**
     * Уничтожаем все что можно, иначе упадет
     * Это значит фрамерт меняется или приложение закрывается
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        try {
            alertService.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void clickByContext(View view) {
        FloatingActionButton fab = (FloatingActionButton) view;
        /*if (arcProgress.isInProgress()) {
            fab.setImageResource(R.drawable.ic_stop);
        }*/
        if (timer == null) {
            timer = new Timer();
            updateVisibleState();
            timer.scheduleAtFixedRate(new ApneaTimerTask(), 0, 1000);
        } else {
            Snackbar.make(view, R.string.snack_stop_session, Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.cancel();
                    timer = null;
                }
            }).show();
        }
        fab.setImageResource(R.drawable.ic_stop);
    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (FloatingActionButton) view;
        fab.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void backPressed() {
        // завершение операций
    }

    private boolean updateVisibleState() {
        TimerPref pr = getNextIntervalAndUpdate();
        if (pr == null) {
            timer.cancel();
            timer = null;
            return false;
        }
        ((TableDetailAdapter) listView.getAdapter()).notifyDataSetChanged();
        prg.setMax(pr.time);
        prg.setBottomText(getString(pr.state == RowState.HOLD ? R.string.timer_hold_lb : R.string.timer_breath_lb));
        prg.setProgress(0);
        alertService.sayState(pr.state);
        return true;
    }


    class ApneaTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (prg.getProgress() + 1 >= prg.getMax()) {
                        if (updateVisibleState()) {
                            return;
                        }
                    }
                    prg.setProgress(prg.getProgress() + 1);
                    alertService.checkNotifications(prg.getMax() - prg.getProgress());
                }
            });
        }
    }

    class TimerPref {
        public int time;

        public RowState state;

        public TimerPref(int time, RowState state) {
            this.time = time;
            this.state = state;
        }
    }
}
