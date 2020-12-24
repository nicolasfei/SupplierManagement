package com.nicolas.supplier.ui.home.salestatistics;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.ArrayList;

public class SaleStatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SaleStatisticsData> saleStatistics;
    private boolean isBusy = false;          //表示list view是否在快速滑动

    public SaleStatisticsAdapter(Context context, ArrayList<SaleStatisticsData> saleStatistics) {
        this.mContext = context;
        this.saleStatistics = saleStatistics;
    }

    @Override
    public int getCount() {
        return this.saleStatistics == null ? 0 : this.saleStatistics.size();
    }

    @Override
    public Object getItem(int position) {
        return this.saleStatistics == null ? null : this.saleStatistics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.sale_statistics_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SaleStatisticsData data = saleStatistics.get(position);

        holder.num.setText((mContext.getString(R.string.pos) + mContext.getString(R.string.colon) + (position + 1)));
        //加载图片
        if (!isBusy) {
            ImageLoadClass.getInstance().displayImage(data.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(data.img, holder.photo, true);
        }

        //新货号+代卖
        String newGoodsValue = "<font color=\"black\"><big>" + mContext.getString(R.string.newGoodsID) + mContext.getString(R.string.colon) + data.goodsId + "(" + data.goodsType + ")" + "</big></font>";
        holder.newGoodsCode.setText(Html.fromHtml(newGoodsValue, Html.FROM_HTML_MODE_COMPACT));

        //旧货号
        String oldGoodsValue = mContext.getString(R.string.oldGoodsID) + mContext.getString(R.string.colon) + data.oldGoodsId;
        holder.oldGoodsCode.setText(Html.fromHtml(oldGoodsValue, Html.FROM_HTML_MODE_COMPACT));

        //已发货数量+总价
        String sendValue = mContext.getString(R.string.sendGoodsCount) + mContext.getString(R.string.colon) + data.sendAmount + " " +
                mContext.getString(R.string.totalPrice) + mContext.getString(R.string.colon) + "￥<font color=\"red\">" + data.sendPrice + "</font>";
        holder.send.setText(Html.fromHtml(sendValue, Html.FROM_HTML_MODE_COMPACT));

        //已销售数量+总价
        String saleValue = mContext.getString(R.string.saleGoodsCount) + mContext.getString(R.string.colon) + data.saleAmount + " " +
                mContext.getString(R.string.totalPrice) + mContext.getString(R.string.colon) + "￥<font color=\"red\">" + data.salePrice + "</font>";
        holder.sale.setText(Html.fromHtml(saleValue, Html.FROM_HTML_MODE_COMPACT));

        //已返货数量+总价
        String backValue = mContext.getString(R.string.backGoodsCount) + mContext.getString(R.string.colon) + data.backAmount + " " +
                mContext.getString(R.string.totalPrice) + mContext.getString(R.string.colon) + "￥<font color=\"red\">" + data.backPrice + "</font>";
        holder.back.setText(Html.fromHtml(backValue, Html.FROM_HTML_MODE_COMPACT));

        //剩余库存+总价
        String surplusValue = mContext.getString(R.string.surplusGoodsCount) + mContext.getString(R.string.colon) + data.inStockAmount + " " +
                mContext.getString(R.string.totalPrice) + mContext.getString(R.string.colon) + "￥<font color=\"red\">" + data.inStockPrice + "</font>";
        holder.surplus.setText(Html.fromHtml(surplusValue, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private static class ViewHolder {
        private TextView num;
        private ImageView photo;
        private TextView newGoodsCode;      //新货号
        private TextView oldGoodsCode;      //旧货号
        private TextView send;              //已发货数量
        private TextView sale;              //已销售数量
        private TextView back;              //已返货数量
        private TextView surplus;           //库存数量

        private ViewHolder(View root) {
            this.num = root.findViewById(R.id.num);
            this.photo = root.findViewById(R.id.photo);
            this.newGoodsCode = root.findViewById(R.id.newGoodsCode);
            this.oldGoodsCode = root.findViewById(R.id.oldGoodsCode);
            this.send = root.findViewById(R.id.send);
            this.sale = root.findViewById(R.id.sale);
            this.back = root.findViewById(R.id.back);
            this.surplus = root.findViewById(R.id.surplus);
        }
    }
}
