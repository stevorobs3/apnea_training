package ru.megazlo.apnea.frag;

import android.app.ListFragment;

import org.androidannotations.annotations.*;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.extend.TableEditorAdapter;
import ru.megazlo.apnea.service.ApneaService;

/** Created by iGurkin on 08.09.2016. */
@EFragment(R.layout.table_editor)
public class TableEditorFragment extends ListFragment {

	private TableEditorAdapter adapter;

	@Bean
	ApneaService apneaService;

	@AfterViews
	void afterView() {
		adapter = new TableEditorAdapter(getActivity());
		getListView().setAdapter(adapter);
	}
}
