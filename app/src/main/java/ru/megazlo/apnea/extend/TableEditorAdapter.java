package ru.megazlo.apnea.extend;

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApneaRow;

/** Created by iGurkin on 08.09.2016. */
public class TableEditorAdapter extends ArrayAdapter<TableApneaRow> {

	public TableEditorAdapter(Context context) {
		super(context, R.layout.table_editor_row);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			holder = new ViewHolder();
			v = LayoutInflater.from(getContext()).inflate(R.layout.table_detail_row, null);
			holder.breathe = (TextView) v.findViewById(R.id.r_time_breathe);
			holder.hold = (TextView) v.findViewById(R.id.r_time_hold);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		TableApneaRow item = this.getItem(position);
		holder.breathe.setText(Utils.formatMS(item.getBreathe()));
		holder.hold.setText(Utils.formatMS(item.getHold()));
		return v;
	}

	private class ViewHolder {
		TextView breathe;
		TextView hold;
	}
}
