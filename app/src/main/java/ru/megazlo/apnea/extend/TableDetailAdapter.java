package ru.megazlo.apnea.extend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.RowState;
import ru.megazlo.apnea.entity.TableApneaRow;

public class TableDetailAdapter extends ArrayAdapter<TableApneaRow> {

    public TableDetailAdapter(Context context) {
        super(context, R.layout.table_detail_row);
    }

    @NonNull
    @Override
    public View getView(int position, View cView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (cView == null) {
            holder = new ViewHolder();
            cView = LayoutInflater.from(getContext()).inflate(R.layout.table_detail_row, null);
            holder.breathe = (TextView) cView.findViewById(R.id.r_time_breathe);
            holder.hold = (TextView) cView.findViewById(R.id.r_time_hold);
            holder.i_breathe = (ImageView) cView.findViewById(R.id.r_img_breathe);
            holder.i_hold = (ImageView) cView.findViewById(R.id.r_img_hold);
            cView.setTag(holder);
        } else {
            holder = (ViewHolder) cView.getTag();
        }
        TableApneaRow item = this.getItem(position);
        holder.breathe.setText(Utils.formatMS(item.getBreathe()));
        holder.hold.setText(Utils.formatMS(item.getHold()));
        holder.i_breathe.setVisibility(item.getState() == RowState.BREATHE ? View.VISIBLE : View.GONE);
        holder.i_hold.setVisibility(item.getState() == RowState.HOLD ? View.VISIBLE : View.GONE);
        return cView;
    }

    private class ViewHolder {
        ImageView i_breathe;
        ImageView i_hold;
        TextView breathe;
        TextView hold;
    }
}
