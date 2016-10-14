package ru.megazlo.apnea.extend;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;

import ru.megazlo.apnea.R;

/** Created by iGurkin on 14.10.2016. */

public abstract class AbstractIndexAdapter<T> extends ArrayAdapter<T> implements IndexAdapter {

	private final static int NO_SELECT_INDEX = -1;
	private final int selectedColor;
	private final int normalColor;
	private int selectedIndex = NO_SELECT_INDEX;

	AbstractIndexAdapter(Context context) {
		super(context, 0);
		selectedColor = context.getResources().getColor(R.color.colorAccent);
		normalColor = context.getResources().getColor(R.color.white);
	}

	public boolean hasSelection() {
		return selectedIndex != NO_SELECT_INDEX;
	}

	public void resetSelection() {
		setSelectedIndex(NO_SELECT_INDEX);
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		notifyDataSetChanged();
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void highlightItem(int position, View result) {
		if (position == selectedIndex) {
			result.setBackgroundColor(selectedColor);
		} else {
			result.setBackgroundColor(normalColor);
		}
	}
}
