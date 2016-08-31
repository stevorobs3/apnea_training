package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.extend.TableDetailAdapter;
import ru.megazlo.apnea.service.ApneaService;

@EFragment(R.layout.table_detail)
public class TableDetailFragment extends Fragment implements FabClickListener {

    private TableApnea tableApnea;

    @Bean
    protected ApneaService apneaService;

    @ViewById(R.id.arc_progress)
    ArcProgress progress;

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
        setStartProgress();
        listView.setAdapter(adapter);
    }

    private void setStartProgress() {
        if (rows != null && rows.size() > 0) {
            final TableApneaRow firstRow = rows.get(0);
            progress.setMax(firstRow.getBreathe());
            progress.setProgress(0);
            int total = 0;
            for (TableApneaRow r : rows) {
                total += r.getBreathe() + r.getHold();
            }
            totalTime.setText("Total time: " + Utils.formatMS(total));
        }
    }

    public void setTableApnea(TableApnea tableApnea) {
        this.tableApnea = tableApnea;
    }

    @Override
    public void clickByContext(View view) {
        FloatingActionButton fab = (FloatingActionButton) view;
        /*if (arcProgress.isInProgress()) {
            fab.setImageResource(R.drawable.ic_stop);
        }*/
        if (timer == null) {
            setStartProgress();
            timer = new Timer();
            timer.scheduleAtFixedRate(new ApneaTimerTask(), 0, 1000);
        } else {
            Snackbar.make(view, "Остановить сессию?", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

    class ApneaTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progress.getProgress() + 1 >= progress.getMax()) {
                        timer.cancel();
                        timer = null;
                    }
                    progress.setProgress(progress.getProgress() + 1);
                }
            });
        }
    }
}
