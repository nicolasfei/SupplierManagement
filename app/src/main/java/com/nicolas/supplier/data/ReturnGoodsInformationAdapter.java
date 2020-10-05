package com.nicolas.supplier.data;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.toollibrary.imageload.ImageLoadClass;
import com.nicolas.toollibrary.Tool;
import com.nicolas.supplier.R;

import java.util.List;

public class ReturnGoodsInformationAdapter extends BaseAdapter {

    private Context mContext;
    private List<ReturnGoodsInformation> returnGoodsList;

    public ReturnGoodsInformationAdapter(Context context, List<ReturnGoodsInformation> returnGoodsList) {
        this.mContext = context;
        this.returnGoodsList = returnGoodsList;
    }

    @Override
    public int getCount() {
        return this.returnGoodsList == null ? 0 : this.returnGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.returnGoodsList == null ? null : this.returnGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.return_goods_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ReturnGoodsInformation returnGoods = returnGoodsList.get(position);
        //加载图片
        ImageLoadClass.getInstance().displayImage(returnGoods.img, holder.photo, false);

        String fIdValue = mContext.getString(R.string.branchID) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + returnGoods.fId + "</big></font>";
        holder.fId.setText(Html.fromHtml(fIdValue, Html.FROM_HTML_MODE_COMPACT));

        String stateValue = "<font color=\"black\">" + returnGoods.state + "</font>";
        holder.state.setText(Html.fromHtml(stateValue, Html.FROM_HTML_MODE_COMPACT));
        holder.state.setBackgroundColor(Color.BLUE);

        String goodsIdValue = mContext.getString(R.string.newGoodsID) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.goodsId + "</font>";
        holder.goodsId.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String oldGoodsIdValue = mContext.getString(R.string.oldGoodsID) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.oldGoodsId + "</font>";
        holder.oldGoodsId.setText(Html.fromHtml(oldGoodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String backNumberValue = mContext.getString(R.string.backNumber) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.backNumber + "</font>";
        holder.backNumber.setText(Html.fromHtml(backNumberValue, Html.FROM_HTML_MODE_COMPACT));

        String backPriceValue = mContext.getString(R.string.backPrice) + mContext.getString(R.string.colon) + "<font color=\"black\">" + mContext.getString(R.string.money) + Tool.float2StringFor2Decimals(returnGoods.backPrice) + "</font>";
        holder.backPrice.setText(Html.fromHtml(backPriceValue, Html.FROM_HTML_MODE_COMPACT));

        String backTotalPriceValue = mContext.getString(R.string.backTotalPrice) + mContext.getString(R.string.colon) + "<font color=\"black\">" + mContext.getString(R.string.money) + Tool.float2StringFor2Decimals(returnGoods.backTotalPrice) + "</font>";
        holder.backTotalPrice.setText(Html.fromHtml(backTotalPriceValue, Html.FROM_HTML_MODE_COMPACT));

        String checkTimeValue = mContext.getString(R.string.returnTime) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.checkTime + "</font>";
        holder.checkTime.setText(Html.fromHtml(checkTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.b_b_BarCode_Id + "</font>";
        holder.code.setText(Html.fromHtml(codeValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = mContext.getString(R.string.remark) + mContext.getString(R.string.colon) + "<font color=\"black\">" + returnGoods.remark + "</font>";
        holder.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView fId;
        private TextView goodsId;
        private TextView oldGoodsId;
        private TextView code;
        private TextView state;
        private TextView checkTime;
        private TextView backNumber;
        private TextView backPrice;
        private TextView backTotalPrice;
        private TextView remark;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);
            this.fId = root.findViewById(R.id.fId);
            this.goodsId = root.findViewById(R.id.goodsId);
            this.oldGoodsId = root.findViewById(R.id.oldGoodsId);
            this.code = root.findViewById(R.id.code);
            this.state = root.findViewById(R.id.state);
            this.checkTime = root.findViewById(R.id.checkTime);
            this.backNumber = root.findViewById(R.id.backNumber);
            this.backPrice = root.findViewById(R.id.backPrice);
            this.backTotalPrice = root.findViewById(R.id.backTotalPrice);
            this.remark = root.findViewById(R.id.remark);
        }
    }
}
