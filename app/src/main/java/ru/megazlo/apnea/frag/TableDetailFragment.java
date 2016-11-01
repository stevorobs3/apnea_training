package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.content.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringRes;

import java.io.Serializable;
import java.util.List;

import ru.megazlo.apnea.ApneaForeService_;
import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.*;
import ru.megazlo.apnea.extend.TableDetailAdapter;
import ru.megazlo.apnea.receivers.DetailFragmentReceiver;
import ru.megazlo.apnea.receivers.ApneaForeReceiver;
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
	@ViewById(R.id.control_pane)
	RelativeLayout buttonPane;

	private List<TableApneaRow> rows;

	private void updateViews(int max, int progress, int row, RowState state) {
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
			prg.setMax(max);
			prg.setBottomText(getString(state == RowState.BREATHE ? R.string.timer_breath_lb : R.string.timer_hold_lb));
			((TableDetailAdapter) listView.getAdapter()).notifyDataSetChanged();
		}
	}

	@AfterViews
	void init() {
		rows = apneaService.getRowsForTable(tableApnea);
		final TableDetailAdapter adapter = new TableDetailAdapter(getActivity());
		adapter.addAll(rows);
		listView.setAdapter(adapter);
		updateTotalTime();
		int iconRes = isMyServiceRunning(ApneaForeService_.class) ? R.drawable.ic_stop : R.drawable.ic_play;
		((FloatingActionButton) getActivity().findViewById(R.id.fab)).setImageResource(iconRes);
	}

	@Click(R.id.img_discard)
	void clickDiscard() {
		sendServiceCommand(ApneaForeReceiver.ACTION_SKIP);
	}

	@Click(R.id.img_stop)
	void clickStop() {
		setViewPlayPause(false);
	}

	@Click(R.id.img_play)
	void clickPlay() {
		setViewPlayPause(true);
	}

	private void setViewPlayPause(boolean isPlayClick) {
		float scale = isPlayClick ? 1 : 0.5f;
		final int visibleChild = isPlayClick ? View.VISIBLE : View.GONE;
		buttonPane.animate().scaleX(scale).scaleY(scale).setDuration(200).start();//.withLayer();
		for (int i = 0; i < buttonPane.getChildCount(); i++) {
			buttonPane.getChildAt(i).setVisibility(visibleChild);
		}
		buttonPane.findViewById(R.id.img_play).setVisibility(isPlayClick ? View.GONE : View.VISIBLE);
	}

	@Click(R.id.img_pause)
	void clickPause() {
		sendServiceCommand(ApneaForeReceiver.ACTION_PAUSE);
		setViewPlayPause(false);
	}

	@Click(R.id.img_add_time)
	void clickAddTime() {
		sendServiceCommand(ApneaForeReceiver.ACTION_ADD_TIME);
	}

	void sendServiceCommand(String name) {
		Intent tb = new Intent(ApneaForeReceiver.ACTION).putExtra(ApneaForeReceiver.ACTION_TYPE, name);
		getActivity().sendBroadcast(tb);
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
			Snackbar.make(view, R.string.snack_stop_session, Snackbar.LENGTH_LONG).setAction(R.string.ok, v -> {
				ApneaForeService_.intent(getActivity().getBaseContext()).stop();
				fab.setImageResource(R.drawable.ic_play);
			}).show();
		} else {
			ApneaForeService_.intent(getActivity().getBaseContext()).extra("table", tableApnea).start();
			fab.setImageResource(R.drawable.ic_stop);
		}
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.GONE);
	}

	@Override
	public boolean backPressed() {
		return true;
	}

	@Receiver(actions = DetailFragmentReceiver.ACTION_UPDATER)
	void detailReceiver(Intent intent) {
		boolean ended = intent.getBooleanExtra(DetailFragmentReceiver.KEY_ENDED, false);
		if (ended) {
			updateTotalTime();
			((FloatingActionButton) getActivity().findViewById(R.id.fab)).setImageResource(R.drawable.ic_play);
			return;
		}
		int max = intent.getIntExtra(DetailFragmentReceiver.KEY_MAX, -1);
		int progress = intent.getIntExtra(DetailFragmentReceiver.KEY_PROGRESS, -1);
		int row = intent.getIntExtra(DetailFragmentReceiver.KEY_ROW, -1);
		RowState state = (RowState) intent.getSerializableExtra(DetailFragmentReceiver.KEY_ROW_TYPE);
		final int tabId = intent.getIntExtra(DetailFragmentReceiver.KEY_ID, -100);
		if (tableApnea.getId() != tabId) {
			Log.i("TableDetailFragment", "need restart service with new parameters");
		} else if (rows != null && rows.size() > 0) {
			updateViews(max, progress, row, state);
		}
	}
}
