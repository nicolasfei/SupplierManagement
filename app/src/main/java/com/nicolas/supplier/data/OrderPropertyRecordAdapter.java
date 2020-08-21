package com.nicolas.supplier.data;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;

public class OrderPropertyRecordAdapter extends BaseAdapter {

    private Context context;
    private List<OrderPropertyRecord> attributes;

    public OrderPropertyRecordAdapter(Context context, List<OrderPropertyRecord> attributes) {
        this.context = context;
        this.attributes = attributes;
    }

    @Override
    public int getCount() {
        return this.attributes == null ? 0 : this.attributes.size();
    }

    @Override
    public Object getItem(int position) {
        return this.attributes == null ? null : this.attributes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.order_goods_information_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderPropertyRecord attribute = attributes.get(position);
        String color = attribute.color;
        holder.color.setText(Html.fromHtml(color, Html.FROM_HTML_MODE_COMPACT));

        String size = attribute.size;
        holder.size.setText(Html.fromHtml(size, Html.FROM_HTML_MODE_COMPACT));

        String num = String.valueOf(attribute.val);
        holder.num.setText(Html.fromHtml(num, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private class ViewHolder {
        TextView color, size, num;

        private ViewHolder(View rootView) {
            this.color = rootView.findViewById(R.id.color);
            this.size = rootView.findViewById(R.id.size);
            this.num = rootView.findViewById(R.id.num);
        }
    }
}
