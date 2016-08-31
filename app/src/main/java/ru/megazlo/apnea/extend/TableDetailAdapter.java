package ru.megazlo.apnea.extend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApneaRow;

public class TableDetailAdapter extends ArrayAdapter<TableApneaRow> {

    private LayoutInflater inflater;

    public TableDetailAdapter(Context context, int resource) {
        super(context, resource);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.table_detail_row, null);
            holder.breathe = (TextView) convertView.findViewById(R.id.r_time_breathe);
            holder.hold = (TextView) convertView.findViewById(R.id.r_time_hold);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TableApneaRow item = this.getItem(position);
        holder.breathe.setText(Utils.formatMS(item.getBreathe()));
        holder.hold.setText(Utils.formatMS(item.getHold()));
        return convertView;
    }

    private class ViewHolder {
        TextView breathe;
        TextView hold;
    }
}
