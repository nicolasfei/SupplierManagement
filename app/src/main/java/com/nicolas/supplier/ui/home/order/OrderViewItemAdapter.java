package com.nicolas.supplier.ui.home.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.supplier.data.OrderPropertyRecord;

import java.util.ArrayList;
import java.util.List;

public class OrderViewItemAdapter extends BaseAdapter {

    private List<String[]> strings;
    private Context mContext;

    public OrderViewItemAdapter(Context context, List<OrderPropertyRecord> propertyRecords) {
        this.mContext = context;
        if (propertyRecords != null && propertyRecords.size() > 0) {
            strings = new ArrayList<>();
            for (int i = 0; i < propertyRecords.size(); ) {
                int sSize = (propertyRecords.size() - i) >= 3 ? 3 : (propertyRecords.size() - i) % 3;
                String[] buf = new String[sSize];
                for (int j = 0; j < buf.length; j++, i++) {
                    OrderPropertyRecord record = propertyRecords.get(i);
                    buf[j] = record.actualColor + "*" + record.actualSize + "*" + record.actualNum;
                }
                strings.add(buf);
            }
        }
    }

    @Override
    public int getCount() {
        return strings == null ? 0 : strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings == null ? null : strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.order_property_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] buf = strings.get(position);
        if (buf.length > 0) {
            holder.property1.setText(buf[0]);
        }
        if (buf.length > 1) {
            holder.property2.setText(buf[1]);
        }
        if (buf.length > 2) {
            holder.property3.setText(buf[2]);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView property1;
        private TextView property2;
        private TextView property3;

        private ViewHolder(View root) {
            this.property1 = root.findViewById(R.id.property1);
            this.property2 = root.findViewById(R.id.property2);
            this.property3 = root.findViewById(R.id.property3);
        }
    }
}
