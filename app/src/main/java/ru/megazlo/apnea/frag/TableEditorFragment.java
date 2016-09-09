package ru.megazlo.apnea.frag;

import android.app.ListFragment;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.extend.TableEditorAdapter;
import ru.megazlo.apnea.service.ApneaService;

/** Created by iGurkin on 08.09.2016. */
@EFragment(R.layout.table_editor)
public class TableEditorFragment extends ListFragment implements FabClickListener {

	@Bean
	ApneaService apneaService;

	@AfterViews
	void afterView() {
		setListAdapter(new TableEditorAdapter(getActivity()));
	}

	@Click(R.id.btn_tab_addRow)
	void clickAdd() {
		getAdapter().add(new TableApneaRow());
	}

	void saveNewTable() {
		final TextView vName = (TextView) getActivity().findViewById(R.id.edit_tab_name);
		final TextView vDescr = (TextView) getActivity().findViewById(R.id.edit_tab_description);
		TableApnea t = new TableApnea();
		t.setTitle(vName.getText().toString());
		t.setDescription(vDescr.getText().toString());

		final TableEditorAdapter adp = getAdapter();
		List<TableApneaRow> rows = new ArrayList<>(adp.getCount());
		for (int i = 0; i < adp.getCount(); i++) {
			TableApneaRow r = adp.getItem(i);
			rows.add(r);
		}
		apneaService.saveNewTable(t, rows);
	}

	private TableEditorAdapter getAdapter() {
		return (TableEditorAdapter) getListAdapter();
	}

	@Override
	public void clickByContext(View view) {
	}

	@Override
	public void modifyToContext(View view) {
	}

	@Override
	public boolean backPressed() {
		return true;
	}
}
