package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Timer;
import java.util.TimerTask;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.ArcProgress;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.service.ApneaPrefs_;

/** Created by iGurkin on 05.09.2016. */
@EFragment(R.layout.record_view)
public class RecordFragment extends Fragment implements FabClickListener {

	private Timer timer;

	@ColorRes
	int colorAccent;

	@Pref
	ApneaPrefs_ pref;

	@ViewById(R.id.arc_record)
	ArcProgress progress;

	@ViewById(R.id.start_record)
	Button button;

	@AfterViews
	void afterView() {
		initProgress();
	}

	@Click(R.id.start_record)
	void startButtonClick() {
		if (timer == null) {
			initProgress();
			timer = new Timer();
			timer.scheduleAtFixedRate(new RecordTask(), 0, 1000);
			setButtonState(R.drawable.ic_stop_white);
		} else {
			stopTimer();
		}
	}

	private void stopTimer() {
		timer.cancel();
		timer = null;
		setButtonState(R.drawable.ic_play_white);
	}

	private void setButtonState(int resId) {
		Drawable img = getActivity().getResources().getDrawable(resId);
		button.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		if (resId == R.drawable.ic_play_white) {
			button.setText(R.string.bt_text_start);
		} else {
			button.setText(R.string.bt_text_stop);
		}
	}

	private void initProgress() {
		progress.setProgress(0);
		final int totalSeconds = Utils.getTotalSeconds(pref.bestRecord().get());
		progress.setMax(totalSeconds);
	}

	@Override
	public void clickByContext(View view) {

	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			stopTimer();
		}
		super.onDestroy();
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.GONE);
	}

	@Override
	public void backPressed() {
		if (timer == null) {
			stopTimer();
		}
	}

	class RecordTask extends TimerTask {
		@Override
		public void run() {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final int totalSeconds = Utils.getTotalSeconds(pref.bestRecord().get());
					if (progress.getProgress() < totalSeconds) {
						progress.setProgress(progress.getProgress() + 1);
					} else if (progress.getProgress() == totalSeconds) {
						if (progress.getUnfinishedStrokeColor() != colorAccent) {
							progress.setUnfinishedStrokeColor(colorAccent);
						}
						progress.setMax(progress.getMax() + 1);
					}
				}
			});
		}
	}
}
