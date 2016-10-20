package ru.megazlo.apnea.extend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;

import java.text.SimpleDateFormat;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.*;

/** Created by iGurkin on 14.10.2016. */
public class TableChartAdapter extends AbstractIndexAdapter<OxiSession> implements IndexAdapter {

	private final SimpleDateFormat frm = new SimpleDateFormat("dd.MM.yyyy hh:mm");

	private final String totalStr;
	private final String dateStr;

	public TableChartAdapter(Context context) {
		super(context);
		totalStr = context.getString(R.string.chart_row_total);
		dateStr = context.getString(R.string.chart_row_date);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		TableChartAdapter.ViewHolder holder;
		if (convertView == null) {
			holder = new TableChartAdapter.ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.chart_row, null);
			//holder.intervals = (TextView) convertView.findViewById(R.id.table_title);
			holder.total = (TextView) convertView.findViewById(R.id.chart_total_time);
			holder.date = (TextView) convertView.findViewById(R.id.chart_date);
			convertView.setTag(holder);
		} else {
			holder = (TableChartAdapter.ViewHolder) convertView.getTag();
		}
		OxiSession item = this.getItem(position);
		//holder.intervals.setText(item.getCount().toString());
		holder.total.setText(String.format(totalStr, Utils.formatMS(item.getTotal())));
		holder.date.setText(String.format(dateStr, frm.format(item.getDate())));
		highlightItem(position, convertView);
		return convertView;
	}

	private class ViewHolder {
		TextView intervals;
		TextView total;
		TextView date;
	}
}
