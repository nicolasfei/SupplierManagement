package com.nicolas.supplier.data;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;

public class GoodsCodePropertyAdapter extends BaseAdapter {

    private List<GoodsCode.Property> properties;
    private Context mContext;
    private OnPropertyStatusChangeListener propertyStatusChangeListener;

    public GoodsCodePropertyAdapter(Context context, List<GoodsCode.Property> properties) {
        this.mContext = context;
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return properties == null ? 0 : properties.size();
    }

    @Override
    public Object getItem(int position) {
        return properties == null ? null : properties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_code_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsCode.Property property = properties.get(position);

        //允许下单
        String isStockValue = "<font color=\"black\">" + mContext.getString(R.string.stockOrder) + "</big></font>";
        holder.stock.setText(Html.fromHtml(isStockValue, Html.FROM_HTML_MODE_COMPACT));
        holder.stock.setChecked(property.isStock.equals(mContext.getString(R.string.stock)));
        holder.stock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (propertyStatusChangeListener != null) {
                    propertyStatusChangeListener.OnGoodsCodeStockChange(property.id, isChecked);
                }
            }
        });

        String gIdValue = "<font color=\"red\">" + property.gId + "</font>";
        holder.gId.setText(Html.fromHtml(gIdValue, Html.FROM_HTML_MODE_COMPACT));

        String colorValue = "<font color=\"yellow\">" + property.color + "</font>";
        holder.color.setText(Html.fromHtml(colorValue, Html.FROM_HTML_MODE_COMPACT));

        String sizeValue = "<font color=\"green\">" + property.size + "</font>";
        holder.size.setText(Html.fromHtml(sizeValue, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    public interface OnPropertyStatusChangeListener {
        void OnGoodsCodeStockChange(String id, boolean isStock);
    }

    public void setOnPropertyStatusChangeListener(OnPropertyStatusChangeListener listener) {
        this.propertyStatusChangeListener = listener;
    }

    private class ViewHolder {
        TextView gId;       //货号
        TextView color;     //颜色
        TextView size;      //尺码
        CheckBox stock;     //允许下单

        private ViewHolder(View root) {
            this.gId = root.findViewById(R.id.gId);
            this.color = root.findViewById(R.id.color);
            this.size = root.findViewById(R.id.size);
            this.stock = root.findViewById(R.id.stock);
        }
    }
}
