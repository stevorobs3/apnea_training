package ru.megazlo.apnea.frag;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import ru.megazlo.apnea.ApneaForeService;
import ru.megazlo.apnea.ApneaForeService_;
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

    @ViewById(R.id.arc_progress)
    ArcProgress prg;
    @ViewById(R.id.total_time)
    TextView totalTime;
    @ViewById(R.id.list_row)
    ListView listView;

    private List<TableApneaRow> rows;

    final DetailFragmentReceiver detailFragmentReceiver = new DetailFragmentReceiver();

    public class DetailFragmentReceiver extends BroadcastReceiver {

        public final static String KEY_UPDATER = "APNEA_DETAIL_UPDATE";

        @Override
        public void onReceive(Context context, Intent intent) {
            int max = intent.getIntExtra("key_max", -1);
            int progress = intent.getIntExtra("key_progress", -1);
            int row = intent.getIntExtra("key_row", -1);
            RowState state = (RowState) intent.getSerializableExtra("key_row_type");
            updateViews(max, progress, row, state);
        }
    }

    private void updateViews(int max, int progress, int row, RowState state) {
        prg.setMax(max);
        prg.setProgress(progress);
        int currRow = -1;
        RowState currStt = null;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getState() != RowState.NONE) {
                currRow = i;
                currStt = rows.get(i).getState();
            }
            rows.get(i).setState(RowState.NONE);
        }
        rows.get(row).setState(state);
        if (currRow != row || currStt != state) {
            ((TableDetailAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    @AfterViews
    void init() {
        getActivity().registerReceiver(detailFragmentReceiver, new IntentFilter(DetailFragmentReceiver.KEY_UPDATER));
        rows = apneaService.getRowsForTable(tableApnea);
        final TableDetailAdapter adapter = new TableDetailAdapter(getActivity(), R.layout.table_detail_row);
        adapter.addAll(rows);
        listView.setAdapter(adapter);
        updateTotalTime();
        if (isMyServiceRunning(ApneaForeService_.class)) {
            ((FloatingActionButton)getActivity().findViewById(R.id.fab)).setImageResource(R.drawable.ic_stop);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(detailFragmentReceiver);
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


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        return ApneaForeService_.RUNNING;
        /*ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;*/
    }

    @Override
    public void clickByContext(View view) {
        final FloatingActionButton fab = (FloatingActionButton) view;
        if (isMyServiceRunning(ApneaForeService_.class)) {
            Snackbar.make(view, R.string.snack_stop_session, Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApneaForeService_.intent(getActivity().getApplication()).stop();
                    fab.setImageResource(R.drawable.ic_play);
                }
            }).show();
        } else {
            ApneaForeService_.intent(getActivity().getApplication()).extra("table", tableApnea).start();
            fab.setImageResource(R.drawable.ic_stop);
        }
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

}
