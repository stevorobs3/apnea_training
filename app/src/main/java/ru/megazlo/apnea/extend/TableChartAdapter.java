package ru.megazlo.apnea.extend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.*;

/** Created by iGurkin on 14.10.2016. */
public class TableChartAdapter extends AbstractIndexAdapter<OxiSession> implements IndexAdapter {

	public TableChartAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		TableChartAdapter.ViewHolder holder;
		if (convertView == null) {
			holder = new TableChartAdapter.ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.chart_row, null);
			//holder.intervals = (TextView) convertView.findViewById(R.id.table_title);
			holder.total = (TextView) convertView.findViewById(R.id.chart_time);
			convertView.setTag(holder);
		} else {
			holder = (TableChartAdapter.ViewHolder) convertView.getTag();
		}
		OxiSession item = this.getItem(position);
		//holder.intervals.setText(item.getCount().toString());
		holder.total.setText(Utils.formatMS(item.getTotal()));
		highlightItem(position, convertView);
		return convertView;
	}

	private class ViewHolder {
		TextView intervals;
		TextView total;
		TextView date;
	}
}
