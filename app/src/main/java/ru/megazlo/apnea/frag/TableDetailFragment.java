package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.entity.TableApnea;

@EFragment(R.layout.table_detail)
public class TableDetailFragment extends Fragment implements FabClickListener {

    private TableApnea tableApnea;

    @ViewById(R.id.arc_progress)
    ArcProgress progress;

    private Timer timer;

    /*@AfterViews
    private void init() {
    }*/

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
            progress.setProgress(0);
            progress.setMax(120);
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
