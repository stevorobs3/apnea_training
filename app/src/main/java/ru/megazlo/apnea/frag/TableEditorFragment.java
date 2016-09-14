package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.extend.TableEditorAdapter;
import ru.megazlo.apnea.receivers.ChangeFragmentReceiver;
import ru.megazlo.apnea.service.ApneaService;

@EFragment(R.layout.table_editor)
public class TableEditorFragment extends Fragment implements FabClickListener {

	@Bean
	ApneaService apneaService;

	@ViewById(R.id.editor_table)
	ListView listView;
	@ViewById(R.id.edit_tab_name)
	TextView vName;
	@ViewById(R.id.edit_tab_description)
	TextView vDescr;

	@AfterViews
	void afterView() {
		final TableEditorAdapter adapter = new TableEditorAdapter(getActivity());
		adapter.add(new TableApneaRow());
		listView.setAdapter(adapter);
	}

	@Click(R.id.btn_tab_addRow)
	void clickAdd() {
		getAdapter().add(new TableApneaRow());
	}

	@Click(R.id.btn_tab_save)
	void saveNewTable() {
		TableApnea t = new TableApnea();
		t.setTitle(vName.getText().toString());
		t.setDescription(vDescr.getText().toString());
		final boolean saveNewTable = apneaService.saveNewTable(t, getAdapter().getAllItems());
		if (saveNewTable) {
			Intent tb = new Intent(ChangeFragmentReceiver.ACTION_FRAGMENT).putExtra(ChangeFragmentReceiver.KEY_FRAG, ChangeFragmentReceiver.KEY_LIST);
			getActivity().sendBroadcast(tb);
		} else {
			Toast.makeText(getActivity(), R.string.err_tab_save, Toast.LENGTH_SHORT).show();
		}
	}

	private TableEditorAdapter getAdapter() {
		return (TableEditorAdapter) listView.getAdapter();
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
