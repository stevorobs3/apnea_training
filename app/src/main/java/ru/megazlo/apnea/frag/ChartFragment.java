package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.view.View;

import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;

import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import ru.megazlo.apnea.R;

/** Created by iGurkin on 10.10.2016. */
@EFragment(R.layout.chart_fragment)
public class ChartFragment extends Fragment implements FabClickListener {

	@ViewById(R.id.chart)
	LineChartView chart;
	@ViewById(R.id.chart_preview)
	PreviewLineChartView chartPreview;

	@AfterViews
	void afterView() {
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		final LineChartData data = new LineChartData();
		Line spo = createLine(1,2,2,2,3,2,4,2);
		spo.setColor(0xffff0000);
		Line hr = createLine(2,1,2,2,2,3,2,4);
		hr.setColor(0xff00ff00);
		data.setLines(Arrays.asList(spo, hr));
		chart.setLineChartData(data);
	}

	private Line createLine(float... vals) {
		Line rez = new Line();
		final ArrayList<PointValue> values = new ArrayList<>();
		final int half = vals.length / 2;
		for (int i = 0; i < half; i++) {
			values.add(new PointValue(vals[i], vals[i + half]));
		}
		rez.setValues(values);
		return rez;
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
}
