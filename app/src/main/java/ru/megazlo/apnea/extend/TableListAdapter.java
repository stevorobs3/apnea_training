package ru.megazlo.apnea.extend;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;

public class TableListAdapter extends AbstractIndexAdapter<TableApnea> {

	public TableListAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.table_list_row, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.img_lungs_icon);
			holder.state = (ImageView) convertView.findViewById(R.id.table_run_state);
			holder.title = (TextView) convertView.findViewById(R.id.table_title);
			holder.description = (TextView) convertView.findViewById(R.id.table_description);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TableApnea item = this.getItem(position);
		holder.title.setText(item.getTitle());
		holder.description.setText(item.getDescription());
		holder.icon.getDrawable().setColorFilter(item.getColor(), PorterDuff.Mode.SRC_ATOP);
		holder.state.setVisibility(item.isRunning() ? View.VISIBLE : View.GONE);
		highlightItem(position, convertView);
		return convertView;
	}

	private class ViewHolder {
		ImageView icon;
		ImageView state;
		TextView title;
		TextView description;
	}
}
