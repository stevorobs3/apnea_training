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
import java.util.Observable;

import ru.megazlo.apnea.ApneaForeService_;
import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.*;
import ru.megazlo.apnea.extend.TableDetailAdapter;
import ru.megazlo.apnea.receivers.*;
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

	private void updateViews(int progress) {
		TableApneaRow cr = null;
		int total = 0;
		for (TableApneaRow r : tableApnea.getRows()) {
			total += r.getHold() + r.getExtHold() + r.getBreathe() + r.getExtBreathe();
			if (r.getState() != RowState.NONE) {
				cr = r;
			}
		}
		if (cr != null) {
			prg.setMax(cr.getState() == RowState.HOLD ? cr.getHold() + cr.getExtHold() : cr.getBreathe() + cr.getExtBreathe());
			prg.setBottomText(getString(cr.getState() == RowState.BREATHE ? R.string.timer_breath_lb : R.string.timer_hold_lb));
		}
		prg.setProgress(progress);
		totalTime.setText(String.format(totalTimeStr, Utils.formatMS(total)));
	}

	@AfterViews
	void init() {
		final TableDetailAdapter adapter = new TableDetailAdapter(getActivity());
		listView.setAdapter(adapter);
		if (ApneaForeService_.STATE == ApneaForeService_.STOP) {
			tableApnea.setRows(apneaService.getRowsForTable(tableApnea));
			adapter.addAll(tableApnea.getRows());
		}
		updateTotalTime();
		setViewPlayPause(ApneaForeService_.STATE == ApneaForeService_.RUN);
	}

	@Click(R.id.img_discard)
	void clickDiscard() {
		sendServiceCommand(ApneaForeReceiver.ACTION_SKIP);
	}

	@Click(R.id.img_stop)
	void clickStop(View view) {
		Snackbar.make(view, R.string.snack_stop_session, Snackbar.LENGTH_LONG).setAction(R.string.ok, v -> {
			ApneaForeService_.intent(getActivity().getBaseContext()).stop();
			setViewPlayPause(false);
		}).show();
	}

	@Click(R.id.img_play)
	void clickPlay() {
		ApneaForeService_.intent(getActivity().getBaseContext()).extra("table", tableApnea).start();
		setViewPlayPause(true);
	}

	private void setViewPlayPause(boolean isPlayClick) {
		float scale = isPlayClick ? 1 : 0.5f;
		final int visibleChild = isPlayClick ? View.VISIBLE : View.GONE;
		buttonPane.animate().scaleX(scale).scaleY(scale).setDuration(200).start();
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
		if (tableApnea.getRows() != null && tableApnea.getRows().size() > 0) {
			prg.setMax(tableApnea.getRows().get(0).getBreathe());
			int total = 0;
			for (TableApneaRow r : tableApnea.getRows()) {
				total += r.getBreathe() + r.getHold();
			}
			totalTime.setText(String.format(totalTimeStr, Utils.formatMS(total)));
		}
	}

	public void setTableApnea(TableApnea tableApnea) {
		this.tableApnea = tableApnea;
	}

	@Override
	public void clickByContext(View view) {
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
			setViewPlayPause(false);
			return;
		}
		//int max = intent.getIntExtra(DetailFragmentReceiver.KEY_MAX, -1);
		int progress = intent.getIntExtra(DetailFragmentReceiver.KEY_PROGRESS, -1);
		//int row = intent.getIntExtra(DetailFragmentReceiver.KEY_ROW, -1);
		//RowState state = (RowState) intent.getSerializableExtra(DetailFragmentReceiver.KEY_ROW_TYPE);
		tableApnea = (TableApnea) intent.getSerializableExtra(DetailFragmentReceiver.KEY_TABLE);
		if (listView != null) {
			final TableDetailAdapter adapter = (TableDetailAdapter) listView.getAdapter();
			adapter.clear();
			adapter.addAll(tableApnea.getRows());
			adapter.notifyDataSetChanged();
			if (tableApnea.getRows() != null && tableApnea.getRows().size() > 0) {
				updateViews(progress);
			}
		}
	}

	@Receiver(actions = OxiReceiver.ACTION)
	void getDataOximeter(Intent intent) {
		final int pulse = intent.getIntExtra(OxiReceiver.PULSE_VAL, -1);
		final int spo = intent.getIntExtra(OxiReceiver.SPO_VAL, -1);
	}
}
