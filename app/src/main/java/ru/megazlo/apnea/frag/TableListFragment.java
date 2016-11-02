package ru.megazlo.apnea.frag;

import android.content.*;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.*;

import org.androidannotations.annotations.*;

import java.util.List;

import ru.megazlo.apnea.ApneaForeService_;
import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.extend.TableListAdapter;
import ru.megazlo.apnea.receivers.ChangeFragmentReceiver;
import ru.megazlo.apnea.receivers.DetailFragmentReceiver;
import ru.megazlo.apnea.service.ApneaService;

@EFragment(R.layout.table_list)
public class TableListFragment extends AbstractListFragment<TableListAdapter> {

	@Bean
	ApneaService apneaService;

	private List<TableApnea> apneaList;

	@AfterViews
	protected void afterView() {
		super.afterView();// легкая декомпозиция, чтоб работали аннотации
	}

	public void loadData() {
		apneaList = apneaService.loadAllTables();
		getAdapter().addAll(apneaList);
	}

	void physicalDelete() {
		TableApnea tb = getAdapter().getItem(getAdapter().getSelectedIndex());
		apneaService.deleteTableById(tb.getId());
		getAdapter().remove(tb);
	}

	void noDeletionClick() {
		if (ApneaForeService_.STATE == ApneaForeService_.RUN) {
			Toast.makeText(getActivity(), R.string.tst_cant_edit, Toast.LENGTH_SHORT).show();
			return;
		}
		sendChangeFragment(ChangeFragmentReceiver.KEY_EDIT, null);
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.VISIBLE);
		FloatingActionButton fab = (FloatingActionButton) view;
		fab.setImageResource(R.drawable.ic_add_plus);
	}

	@Receiver(actions = DetailFragmentReceiver.ACTION_UPDATER)
	void detailReceiver(Intent intent) {
		boolean ended = intent.getBooleanExtra(DetailFragmentReceiver.KEY_ENDED, false);
		final TableApnea tbl = (TableApnea) intent.getSerializableExtra(DetailFragmentReceiver.KEY_TABLE);
		for (TableApnea t : apneaList) {
			t.setRunning(false);
			if (!ended && t.getId().equals(tbl.getId()) && !t.isRunning()) {
				t.setRunning(true);
				((TableListAdapter) getListAdapter()).notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TableApnea table = (TableApnea) parent.getItemAtPosition(position);
		sendChangeFragment(ChangeFragmentReceiver.KEY_DETAIL, table);
	}
}
