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

public class OrderPrintAdapter extends BaseAdapter {
    private static final String TAG = "OrderPrintAdapter";
    private Context context;
    private List<OrderInformation> orders;

    public OrderPrintAdapter(Context context, List<OrderInformation> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders == null ? 0 : orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders == null ? null : orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.order_print_information, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderInformation order = this.orders.get(position);
        //编号
        holder.w0.setText(Html.fromHtml(context.getString(R.string.codeID + 1) + context.getString(R.string.colon) + position, Html.FROM_HTML_MODE_COMPACT));

        //库房，单属性
        String w1Value = context.getString(R.string.print_warehouse) + context.getString(R.string.colon) + "<font color=\"black\">" + order.storeRoomName + "</font>" + "<br>" +
                context.getString(R.string.print_order_class) + context.getString(R.string.colon) + "<font color=\"black\">" + order.orderType.getType() + "</font>";
        holder.w1.setText(Html.fromHtml(w1Value, Html.FROM_HTML_MODE_COMPACT));

        //旧货号，新货号
        String w2Value = context.getString(R.string.print_old_goods_code) + context.getString(R.string.colon) + "<font color=\"black\">" + order.oldGoodsId + "</font>" + "<br>" +
                context.getString(R.string.print_new_goods_code) + context.getString(R.string.colon) + "<font color=\"black\">" + order.goodsId + "</font>";
        holder.w2.setText(Html.fromHtml(w2Value, Html.FROM_HTML_MODE_COMPACT));

        //商品类别，备注
        String w3Value = context.getString(R.string.print_goods_class) + context.getString(R.string.colon) + "<font color=\"black\">" + order.goodsClassName + "</font>" + "<br>" +
                context.getString(R.string.print_order_id) + context.getString(R.string.colon) + "<font color=\"black\">" + order.id + "</font>" + "<br>" +
                context.getString(R.string.print_remark) + context.getString(R.string.colon) + "<font color=\"black\">" + order.remark + "</font>";
        holder.w3.setText(Html.fromHtml(w3Value, Html.FROM_HTML_MODE_COMPACT));

        //分店编号
        String b1Value = context.getString(R.string.print_branch_code) + context.getString(R.string.colon) + "<font color=\"black\">" + order.fId + "</font>";
        holder.b1.setText(Html.fromHtml(b1Value, Html.FROM_HTML_MODE_COMPACT));

        //下单日期，截至日期
        String b2Value = context.getString(R.string.print_order_date) + context.getString(R.string.colon) + "<font color=\"black\">" + order.createTime + "</font>" + "<br>" +
                context.getString(R.string.print_invalid_date) + context.getString(R.string.colon) + "<font color=\"black\">" + order.inValidTime + "</font>";
        holder.b2.setText(Html.fromHtml(b2Value, Html.FROM_HTML_MODE_COMPACT));

        //发货数量
        String b3Value = context.getString(R.string.print_send_amount) + context.getString(R.string.colon) + "<font color=\"black\">" + order.sendAmount + "</font>";
        holder.b3.setText(Html.fromHtml(b3Value, Html.FROM_HTML_MODE_COMPACT));

        //颜色尺码数量
        String b3_1Value = "<font color=\"red\">" + "&#12288" + context.getString(R.string.print_size) + "</font>" + "<br>";
        String b3_2Value = "<font color=\"blue\">" + "&#12288" + context.getString(R.string.print_attr) + "</font>" + "<br>";
        String b3_3Value = "<font color=\"green\">" + "&#12288" + context.getString(R.string.print_num) + "</font>" + "<br>";
        for (OrderPropertyRecord attr : order.propertyRecords) {
            b3_1Value += "\u3000" + attr.actualSize + "</font>" + "<br>";
            b3_2Value += "\u3000" + attr.actualColor + "</font>" + "<br>";
            b3_3Value += "\u3000" + "x" + attr.actualNum + "</font>" + "<br>";
        }
        holder.b3_1.setText(Html.fromHtml(b3_1Value, Html.FROM_HTML_MODE_COMPACT));
        holder.b3_2.setText(Html.fromHtml(b3_2Value, Html.FROM_HTML_MODE_COMPACT));
        holder.b3_3.setText(Html.fromHtml(b3_3Value, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private class ViewHolder {

        private TextView w0, w1, w2, w3;
        private TextView b1, b2, b3, b3_1, b3_2, b3_3;

        private ViewHolder(View root) {
            this.b1 = root.findViewById(R.id.b1);       //分店编号
            this.b2 = root.findViewById(R.id.b2);       //下单日期，截至日期
            this.b3 = root.findViewById(R.id.b3);       //发货数量
            this.b3_1 = root.findViewById(R.id.b3_1);       //尺码
            this.b3_2 = root.findViewById(R.id.b3_2);       //属性
            this.b3_3 = root.findViewById(R.id.b3_3);       //数量

            this.w0 = root.findViewById(R.id.w0);       //编号
            this.w1 = root.findViewById(R.id.w1);       //库房，单属性
            this.w2 = root.findViewById(R.id.w2);       //旧货号，新货号
            this.w3 = root.findViewById(R.id.w3);       //备注
        }
    }
}
