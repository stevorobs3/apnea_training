package ru.megazlo.apnea.frag;

import android.app.ListFragment;
import android.content.*;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.*;

import org.androidannotations.annotations.*;

import java.util.List;

import ru.megazlo.apnea.ApneaForeService_;
import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.extend.TableListAdapter;
import ru.megazlo.apnea.service.ApneaService;

@EFragment(R.layout.table_list)
public class TableListFragment extends ListFragment implements FabClickListener {

	@Bean
	ApneaService apneaService;

	private AdapterView.OnItemClickListener listener;

	private List<TableApnea> apneaList;

	@AfterViews
	protected void afterView() {
		Context ctx = this.getActivity();
		setListAdapter(new TableListAdapter(ctx, 0));
		apneaList = apneaService.loadAllTables();
		getAdapter().addAll(apneaList);
		if (listener != null) {
			getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			getListView().setSelector(R.drawable.list_color_selector);
			getListView().setOnItemClickListener(listener);
			getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					getAdapter().setSelectedIndex(position);
					FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab));
					fab.setImageResource(R.drawable.ic_delete);
					return true;
				}
			});
		}
		getActivity().registerReceiver(detailFragmentReceiver, new IntentFilter(DetailFragmentReceiver.ACTION_UPDATER));
	}

	private TableListAdapter getAdapter() {
		return (TableListAdapter) getListAdapter();
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (detailFragmentReceiver != null) {
			getActivity().unregisterReceiver(detailFragmentReceiver);
		}
	}

	private void deleteSelectedItem() {
		TableApnea tb = getAdapter().getItem(getAdapter().getSelectedIndex());
		apneaService.deleteTableById(tb.getId());
		getAdapter().remove(tb);
		FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab));
		fab.setImageResource(R.drawable.ic_add_plus);
	}

	@Override
	public void clickByContext(View view) {
		if (ApneaForeService_.RUNNING) {
			Toast.makeText(getActivity(), R.string.tst_cant_edit, Toast.LENGTH_SHORT).show();
			return;
		}
		if (getAdapter().hasSelection()) {
			deleteSelectedItem();
			return;
		}
		Snackbar.make(view, R.string.snack_tab_dev, Snackbar.LENGTH_SHORT).setAction(R.string.ok, null).show();
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.VISIBLE);
		FloatingActionButton fab = (FloatingActionButton) view;
		fab.setImageResource(R.drawable.ic_add_plus);
	}

	@Override
	public boolean backPressed() {
		if (getAdapter().hasSelection()) {
			getAdapter().resetSelection();
			FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab));
			fab.setImageResource(R.drawable.ic_add_plus);
			return false;
		}
		return true;
	}

	private DetailFragmentReceiver detailFragmentReceiver = new DetailFragmentReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean ended = intent.getBooleanExtra(KEY_ENDED, false);
			final int tabId = intent.getIntExtra(KEY_ID, -100);
			for (TableApnea t : apneaList) {
				t.setRunning(false);
				if (!ended && t.getId() == tabId && !t.isRunning()) {
					t.setRunning(true);
					((TableListAdapter) getListAdapter()).notifyDataSetChanged();
				}
			}
		}
	};
}
