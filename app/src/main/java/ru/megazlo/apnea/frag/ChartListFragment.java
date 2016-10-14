package ru.megazlo.apnea.frag;

import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.*;

import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.OxiSession;
import ru.megazlo.apnea.extend.TableChartAdapter;
import ru.megazlo.apnea.receivers.ChangeFragmentReceiver;
import ru.megazlo.apnea.service.ApneaService;

/** Created by iGurkin on 13.10.2016. */
@EFragment(R.layout.chart_list_fragment)
public class ChartListFragment extends AbstractListFragment<TableChartAdapter> {

	@Bean
	ApneaService apneaService;

	private List<OxiSession> apneaSessions;

	@AfterViews
	protected void afterView() {
		super.afterView();// легкая декомпозиция, чтоб работали аннотации
	}

	@Override
	public void loadData() {
		apneaSessions = apneaService.loadAllSessions();
		getAdapter().addAll(apneaSessions);
	}

	@Override
	void physicalDelete() {
		OxiSession oss = getAdapter().getItem(getAdapter().getSelectedIndex());
		apneaService.deleteTableById(oss.getId());
		getAdapter().remove(oss);
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OxiSession sess = (OxiSession) parent.getItemAtPosition(position);
		sendChangeFragment(ChangeFragmentReceiver.KEY_OXI_VIEW, sess);
	}
}
