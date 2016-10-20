package ru.megazlo.apnea.frag;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.io.Serializable;
import java.lang.reflect.*;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.extend.AbstractIndexAdapter;
import ru.megazlo.apnea.receivers.ChangeFragmentReceiver;

/** Created by iGurkin on 14.10.2016. */
@SuppressWarnings("unchecked")
public abstract class AbstractListFragment<T extends AbstractIndexAdapter> extends ListFragment implements FabClickListener, AdapterView.OnItemClickListener {

	private Class<T> adapterClass;

	public AbstractListFragment() {
		this.adapterClass = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected void afterView() {
		try {
			final Constructor<T> constructor = adapterClass.getConstructor(Context.class);
			setListAdapter(constructor.newInstance(getActivity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadData();
		getListView().setOnItemClickListener(this);
		getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		getListView().setSelector(R.drawable.list_color_selector);
		getListView().setOnItemLongClickListener((parent, view, position, id) -> {
			getAdapter().setSelectedIndex(position);
			FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab));
			fab.setImageResource(R.drawable.ic_delete);
			return true;
		});
	}

	public abstract void loadData();

	private void deleteSelectedItem() {
		physicalDelete();
		getAdapter().resetSelection();
		FloatingActionButton fab = ((FloatingActionButton) getActivity().findViewById(R.id.fab));
		fab.setImageResource(R.drawable.ic_add_plus);
	}

	abstract void physicalDelete();

	@Override
	public void clickByContext(View view) {
		if (!hasDelete()) {
			noDeletionClick();
		}
	}

	void noDeletionClick() {
	}

	private boolean hasDelete() {
		if (getAdapter().hasSelection()) {
			deleteSelectedItem();
			return true;
		}
		return false;
	}

	T getAdapter() {
		return (T) getListAdapter();
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

	void sendChangeFragment(String name, Serializable tab) {
		Intent tb = new Intent(ChangeFragmentReceiver.ACTION_FRAGMENT);
		tb.putExtra(ChangeFragmentReceiver.KEY_FRAG, name);
		tb.putExtra(ChangeFragmentReceiver.KEY_TABLE, tab);
		getActivity().sendBroadcast(tb);
	}
}
