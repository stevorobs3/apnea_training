package ru.megazlo.apnea.extend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApneaRow;

/** Created by iGurkin on 08.09.2016. */
public class TableEditorAdapter extends ArrayAdapter<TableApneaRow> {

	public TableEditorAdapter(Context context) {
		super(context, R.layout.table_editor_row);
	}

	@NonNull
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

			holder.breathe.addTextChangedListener(new TextWatcherSimple(holder.breathe) {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					ViewHolder vh = (ViewHolder) ((RelativeLayout) ed.getParent()).getTag();
					if (vh.pos < getCount()) {
						int seconds = getStringSeconds(ed.getText().toString());
						getItem(vh.pos).setBreathe(seconds);
					}
				}
			});
			holder.hold.addTextChangedListener(new TextWatcherSimple(holder.hold) {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					ViewHolder vh = (ViewHolder) ((RelativeLayout) ed.getParent()).getTag();
					if (vh.pos < getCount()) {
						int seconds = getStringSeconds(ed.getText().toString());
						getItem(vh.pos).setHold(seconds);
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
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.pos = position;

		TableApneaRow item = this.getItem(position);
		holder.breathe.setText(String.valueOf(item.getBreathe()));
		holder.hold.setText(String.valueOf(item.getHold()));
		return v;
	}

	private int getStringSeconds(String str) {
		int seconds = 0;
		if (str.length() > 0) {
			try {
				seconds = Integer.parseInt(str.replace(',', '.'));
			} catch (NumberFormatException e) {
				Toast.makeText(getContext(), R.string.invalid_num_format, Toast.LENGTH_SHORT).show();
			}
		}
		return seconds;
	}

	public List<TableApneaRow> getAllItems() {
		List<TableApneaRow> rez = new ArrayList<>(getCount());
		for (int i = 0; i < getCount(); i++) {
			TableApneaRow r = getItem(i);
			rez.add(r);
		}
		return rez;
	}

	private abstract class TextWatcherSimple implements TextWatcher {

		final EditText ed;

		TextWatcherSimple(View v) {
			this.ed = (EditText) v;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	private static class ViewHolder {
		int pos;
		EditText breathe;
		EditText hold;
		ImageView imgDelete;
	}
}
