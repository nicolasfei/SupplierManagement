package com.nicolas.supplier.data;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;

public class OrderGoodsCountAdapter extends BaseAdapter {

    private Context mContext;
    private List<OrderStatistics> goodsCounts;

    public OrderGoodsCountAdapter(Context context, List<OrderStatistics> goodsCounts) {
        this.mContext = context;
        this.goodsCounts = goodsCounts;
    }

    @Override
    public int getCount() {
        return goodsCounts == null ? 0 : goodsCounts.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsCounts == null ? null : goodsCounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_goods_count_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //OrderGoodsCount
        OrderStatistics count = goodsCounts.get(position);

        //新货号
        String goodsID = mContext.getString(R.string.newGoodsID) + mContext.getString(R.string.colon) + "<font color=\"black\">" + count.goodsID + "</font>";
        holder.goodsID.setText(Html.fromHtml(goodsID, Html.FROM_HTML_MODE_COMPACT));
        //旧货号
        String oldGoodsID = mContext.getString(R.string.oldGoodsID) + mContext.getString(R.string.colon) + "<font color=\"black\">" + count.oldGoodsID + "</font>";
        holder.oldGoodsID.setText(Html.fromHtml(oldGoodsID, Html.FROM_HTML_MODE_COMPACT));
        //颜色尺码数量
        holder.property.removeAllViews();
        for (OrderStatisticsProperty property : count.properties) {
            View item = LayoutInflater.from(mContext).inflate(R.layout.goods_code_count_property_item, null, false);
            TextView colorSize = item.findViewById(R.id.colorSize);
            TextView num = item.findViewById(R.id.num);
            colorSize.setText((property.color + "*" + property.size));
            num.setText(String.valueOf(property.num));
            holder.property.addView(item);
        }
        //合计
        String totalV = "<font color=\"red\">" + mContext.getString(R.string.total) + mContext.getString(R.string.colon) + count.getTotal() + "</font>";
        holder.total.setText(Html.fromHtml(totalV, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    private static class ViewHolder {
        private TextView goodsID;
        private TextView oldGoodsID;
        private LinearLayout property;
        private TextView total;

        private ViewHolder(View root) {
            this.goodsID = root.findViewById(R.id.goodsID);
            this.oldGoodsID = root.findViewById(R.id.oldGoodsID);
            this.property = root.findViewById(R.id.property);
            this.total = root.findViewById(R.id.total);
        }
    }
}
