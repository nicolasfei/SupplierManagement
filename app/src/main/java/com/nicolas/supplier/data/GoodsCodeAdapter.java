package com.nicolas.supplier.data;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.toollibrary.imageload.ImageLoadClass;
import com.nicolas.supplier.R;

import java.util.List;

public class GoodsCodeAdapter extends BaseAdapter {

    private List<GoodsCode> goodsCodeList;
    private Context mContext;
    private boolean isBusy = false;          //表示list view是否在快速滑动
    private OnGoodsCodeStatusChangeListener listener;

    //上一次点击时间---点击防抖
    private static long lastClickTime = 0;
    private static final int INTERVAL_TIME = 600;

    public GoodsCodeAdapter(Context context, List<GoodsCode> goodsCodeList) {
        this.mContext = context;
        this.goodsCodeList = goodsCodeList;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    @Override
    public int getCount() {
        return this.goodsCodeList == null ? 0 : this.goodsCodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.goodsCodeList == null ? null : this.goodsCodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_code_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsCode goodsCode = goodsCodeList.get(position);

        //编号
        String posValue = mContext.getString(R.string.pos) + mContext.getString(R.string.colon) + (position + 1);
        holder.pos.setText(posValue);

        //加载图片
        if (!isBusy) {
            ImageLoadClass.getInstance().displayImage(goodsCode.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(goodsCode.img, holder.photo, true);
        }

        //允许下单
        String isStockValue = "<font color=\"black\">" + mContext.getString(R.string.stockOrder) + "</big></font>";
        holder.isStock.setOnCheckedChangeListener(null);
        holder.isStock.setText(Html.fromHtml(isStockValue, Html.FROM_HTML_MODE_COMPACT));
        holder.isStock.setChecked(goodsCode.isStock.equals((mContext.getString(R.string.stock))));
        holder.isStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.OnGoodsCodeStockChange(goodsCode.id, null, isChecked);
                }
            }
        });

        String describeValue = "<font color=\"blue\"><big>" + mContext.getString(R.string.createTime) + mContext.getString(R.string.colon) + goodsCode.createTime + "</big></font>";
        holder.describe.setText(Html.fromHtml(describeValue, Html.FROM_HTML_MODE_COMPACT));

        String seasonNameValue = mContext.getString(R.string.seasonName) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + goodsCode.seasonName + "</big></font>";
        holder.seasonName.setText(Html.fromHtml(seasonNameValue, Html.FROM_HTML_MODE_COMPACT));

        String customerValue = mContext.getString(R.string.goodsClass) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + goodsCode.goodsClassName + "</big></font>";
        holder.customer.setText(Html.fromHtml(customerValue, Html.FROM_HTML_MODE_COMPACT));

        String oldGoodsIdValue = mContext.getString(R.string.oldGoodsId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + goodsCode.oldGoodsId + "</big></font>";
        holder.oldGoodsId.setText(Html.fromHtml(oldGoodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + goodsCode.goodsId + "</big></font>";
        holder.goodsId.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));
        holder.goodsId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                    if (goodsCode.hasQueryProperties) {
                        if (!goodsCode.showProperties) {
                            goodsCode.showProperties = true;
                            holder.property.setVisibility(View.VISIBLE);
                            holder.goodsId.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_sj_up), null);
                        } else {
                            goodsCode.showProperties = false;
                            holder.property.setVisibility(View.GONE);
                            holder.goodsId.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_sj_down), null);
                        }
                    } else {
                        if (listener != null) {
                            listener.OnGoodsCodePropertyQuery(goodsCode.id);
                        }
                    }
                    lastClickTime = System.currentTimeMillis();
                }
            }
        });
        holder.goodsId.setClickable(true);

//        String inPriceValue = mContext.getString(R.string.bid) + mContext.getString(R.string.colon) + "<font color=\"red\"><big>" + mContext.getString(R.string.money) + goodsCode.inPrice + "</big></font>";
//        holder.inPrice.setText(Html.fromHtml(inPriceValue, Html.FROM_HTML_MODE_COMPACT));

        String originalPriceValue = mContext.getString(R.string.originalPrice) + mContext.getString(R.string.colon) + "<font color=\"green\"><big>" + mContext.getString(R.string.money) + goodsCode.originalPrice + "</big></font>";
        holder.originalPrice.setText(Html.fromHtml(originalPriceValue, Html.FROM_HTML_MODE_COMPACT));

//        String orderPriceValue = mContext.getString(R.string.orderPrice) + mContext.getString(R.string.colon) + "<font color=\"blue\"><big>" + mContext.getString(R.string.money) + goodsCode.orderPrice + "</big></font>";
//        holder.orderPrice.setText(Html.fromHtml(orderPriceValue, Html.FROM_HTML_MODE_COMPACT));

        String deliveryDateValue = mContext.getString(R.string.deliveryDays) + mContext.getString(R.string.colon) + "<font color=\"red\"><big>" + goodsCode.deliveryDate + "</big></font>";
        holder.deliveryDate.setText(Html.fromHtml(deliveryDateValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsTypeValue = mContext.getString(R.string.goodsType) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + goodsCode.goodsType + "</big></font>";
        holder.goodsType.setText(Html.fromHtml(goodsTypeValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = "<font color=\"black\"><big>" + mContext.getString(R.string.remark) + "</big></font>" + mContext.getString(R.string.colon) + goodsCode.remark;
        holder.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));


        //加载属性
        //先移除之前view中缓存的数据
        holder.property.removeAllViews();
        //添加属性
        for (final GoodsCode.Property property : goodsCode.properties) {
            View layout = LayoutInflater.from(this.mContext).inflate(R.layout.goods_code_property_item, null, false);
            //允许下单
            CheckBox stock = layout.findViewById(R.id.stock);
            String stockValue = "<font color=\"black\">" + mContext.getString(R.string.stockOrder) + "</big></font>";
            stock.setText(Html.fromHtml(stockValue, Html.FROM_HTML_MODE_COMPACT));
            stock.setChecked(property.isStock.equals(mContext.getString(R.string.stock)));
            stock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        listener.OnGoodsCodeStockChange(goodsCode.id, property.id, isChecked);
                    }
                }
            });

            TextView gId = layout.findViewById(R.id.gId);
            String gIdValue = "<font color=\"red\">" + property.id + "</font>";
            gId.setText(Html.fromHtml(gIdValue, Html.FROM_HTML_MODE_COMPACT));

            TextView color = layout.findViewById(R.id.color);
            String colorValue = "<font color=\"yellow\">" + property.color + "</font>";
            color.setText(Html.fromHtml(colorValue, Html.FROM_HTML_MODE_COMPACT));

            TextView size = layout.findViewById(R.id.size);
            String sizeValue = "<font color=\"green\">" + property.size + "</font>";
            size.setText(Html.fromHtml(sizeValue, Html.FROM_HTML_MODE_COMPACT));
            holder.property.addView(layout);
        }

        //根据showProperties来决定是否显示property
        if (goodsCode.showProperties) {
            holder.property.setVisibility(View.VISIBLE);
            holder.goodsId.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_sj_up), null);
        } else {
            holder.property.setVisibility(View.GONE);
            holder.goodsId.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_sj_down), null);
        }

        return convertView;
    }

    public interface OnGoodsCodeStatusChangeListener {
        //货号下单状态状态更新
        void OnGoodsCodeStockChange(String goodsCodeID, String goodsCodePropertyID, boolean isStock);

        //货号属性查询
        void OnGoodsCodePropertyQuery(String goodsCodeID);
    }

    public void setOnGoodsCodeStatusChangeListener(OnGoodsCodeStatusChangeListener listener) {
        this.listener = listener;
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView pos;
        private TextView describe;          //货号描述：供货商编号/货物类别名称/创建时间----9527丫头弟弟/衬衫/2020-07-10 09:41
        //private TextView supplierId;        //供货商编号
        //private TextView goodsClassName;    //货物类别名称
        private TextView seasonName;        //季节装
        private TextView customer;          //顾客群
        private TextView oldGoodsId;        //旧货号
        private TextView goodsId;           //新货号

//        private TextView inPrice;           //进价
        private TextView originalPrice;     //原进价
//        private TextView orderPrice;        //订货价
        private TextView deliveryDate;      //交货天数

        private TextView goodsType;         //货号类型
        private CheckBox isStock;           //允许下单
        //private TextView createTime;        //创建时间--2020-07-10 09:41
        //private TextView valid;             //启用
        private TextView remark;            //备注
        private LinearLayout property;      //属性

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);
            this.pos = root.findViewById(R.id.pos);
            this.describe = root.findViewById(R.id.describe);
            this.seasonName = root.findViewById(R.id.seasonName);
            this.customer = root.findViewById(R.id.customer);
            this.oldGoodsId = root.findViewById(R.id.oldGoodsId);
            this.goodsId = root.findViewById(R.id.goodsId);
//            this.inPrice = root.findViewById(R.id.inPrice);
            this.originalPrice = root.findViewById(R.id.originalPrice);
//            this.orderPrice = root.findViewById(R.id.orderPrice);
            this.deliveryDate = root.findViewById(R.id.deliveryDate);
            this.goodsType = root.findViewById(R.id.goodsType);
            this.isStock = root.findViewById(R.id.isStock);
            this.remark = root.findViewById(R.id.remark);
            this.property = root.findViewById(R.id.property);
        }
    }
}
