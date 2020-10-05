package com.nicolas.supplier.data;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;

public class OrderGoodsStatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private List<OrderStatistics> goodsCounts;

    public OrderGoodsStatisticsAdapter(Context context, List<OrderStatistics> goodsCounts) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_goods_statistices_item, parent, false);
            convertView.setBackgroundColor(Color.GRAY);
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
//        //货物类别ID
//        String goodsClassId = mContext.getString(R.string.goodsClassId) + mContext.getString(R.string.colon) + "<font color=\"black\">" + count.goodsClassId + "</font>";
//        holder.goodsClassId.setText(Html.fromHtml(goodsClassId, Html.FROM_HTML_MODE_COMPACT));
//        //货物类别名
//        String goodsClassName = mContext.getString(R.string.goodsClassName) + mContext.getString(R.string.colon) + "<font color=\"black\">" + count.goodsClassName + "</font>";
//        holder.goodsClassName.setText(Html.fromHtml(goodsClassName, Html.FROM_HTML_MODE_COMPACT));
//        //颜色尺码
//        String colorSize = "<font color=\"black\">" + count.color + "*" + count.size + "</font>";
//        holder.colorSize.setText(Html.fromHtml(colorSize, Html.FROM_HTML_MODE_COMPACT));
//        //数量
//        String num = "<font color=\"black\">" + "x" + count.totalAll + "</font>";
//        holder.num.setText(Html.fromHtml(num, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    private static class ViewHolder {
        private TextView goodsID;
        private TextView oldGoodsID;
        private TextView goodsClassId;
        private TextView goodsClassName;
        private TextView colorSize;
        private TextView num;

        private ViewHolder(View root) {
            this.goodsID = root.findViewById(R.id.goodsID);
            this.oldGoodsID = root.findViewById(R.id.oldGoodsID);

            this.goodsClassId = root.findViewById(R.id.goodsClassId);
            this.goodsClassName = root.findViewById(R.id.goodsClassName);

            this.colorSize = root.findViewById(R.id.colorSize);
            this.num = root.findViewById(R.id.num);
        }
    }
}
