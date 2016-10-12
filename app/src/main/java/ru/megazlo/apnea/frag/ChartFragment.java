package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.view.View;

import org.androidannotations.annotations.EFragment;

import ru.megazlo.apnea.R;

/** Created by iGurkin on 10.10.2016. */
@EFragment(R.layout.chart_fragment_layout)
public class ChartFragment extends Fragment implements FabClickListener {


	@Override
	public void clickByContext(View view) {

	}

	@Override
	public void modifyToContext(View view) {
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public boolean backPressed() {
		return false;
	}
}
