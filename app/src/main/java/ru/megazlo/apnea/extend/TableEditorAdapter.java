package ru.megazlo.apnea.extend;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApneaRow;

/** Created by iGurkin on 08.09.2016. */
public class TableEditorAdapter extends ArrayAdapter<TableApneaRow> {

	public TableEditorAdapter(Context context) {
		super(context, R.layout.table_editor_row);
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			holder = new ViewHolder();
			v = LayoutInflater.from(getContext()).inflate(R.layout.table_editor_row, null);
			holder.breathe = (EditText) v.findViewById(R.id.ed_time_breathe);
			holder.hold = (EditText) v.findViewById(R.id.ed_time_hold);
			holder.imgDelete = (ImageView) v.findViewById(R.id.ed_img_delete);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.pos = position;

		holder.breathe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ViewHolder vh = (ViewHolder) ((RelativeLayout) v.getParent()).getTag();
					if (vh.pos < getCount()) {
						final EditText et = (EditText) v;
						getItem(vh.pos).setBreathe(Integer.parseInt(et.getText().toString()));
					}
				}
			}
		});

		holder.hold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					ViewHolder vh = (ViewHolder) ((RelativeLayout) v.getParent()).getTag();
					if (vh.pos < getCount()) {
						final EditText et = (EditText) v;
						getItem(vh.pos).setHold(Integer.parseInt(et.getText().toString()));
					}
				}
			}
		});
		holder.imgDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewHolder vh = (ViewHolder) ((RelativeLayout) v.getParent()).getTag();
				TableEditorAdapter.this.remove(getItem(vh.pos));
			}
		});

		TableApneaRow item = this.getItem(position);
		holder.breathe.setText(Integer.toString(item.getBreathe()));
		holder.hold.setText(Integer.toString(item.getHold()));
		return v;
	}

	private class ViewHolder {
		int pos;
		EditText breathe;
		EditText hold;
		ImageView imgDelete;
	}
}
