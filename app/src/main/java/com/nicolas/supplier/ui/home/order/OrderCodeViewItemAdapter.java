package com.nicolas.supplier.ui.home.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.supplier.data.OrderGoodsIDClass;
import com.nicolas.supplier.data.OrderInformationAdapter;
import com.nicolas.supplier.data.OrderPropertyRecord;

import java.util.ArrayList;
import java.util.List;

public class OrderCodeViewItemAdapter extends BaseAdapter {

    private List<OrderGoodsIDClass> orderGoods;
    private Context mContext;

    private OnOrderCodeSubmitListener orderCodeSubmitListener;

    public OrderCodeViewItemAdapter(Context context, List<OrderGoodsIDClass> orderGoods) {
        this.mContext = context;
        this.orderGoods = orderGoods;
    }

    @Override
    public int getCount() {
        return orderGoods == null ? 0 : orderGoods.size();
    }

    @Override
    public Object getItem(int position) {
        return orderGoods == null ? null : orderGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_order_code_switen, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderGoodsIDClass og = orderGoods.get(position);
        holder.newGoodID.setText(og.Goodsld);
        holder.oldGoodID.setText(og.OldGoodsld);
        holder.count.setText(String.valueOf(og.SendAmount));
        if (og.switen){
            holder.submit.setEnabled(false);
            holder.submit.setText("已确认");
        }else{
            holder.submit.setEnabled(true);
            holder.submit.setText("确认");
            holder.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderCodeSubmitListener!=null){
                        orderCodeSubmitListener.onOrderCodeSubmit(og);
                    }
                }
            });
        }

        return convertView;
    }

    public interface OnOrderCodeSubmitListener {
        void onOrderCodeSubmit(OrderGoodsIDClass orderGood);
    }
    public void setOnOrderCodeSubmitListener(OnOrderCodeSubmitListener listener) {
        this.orderCodeSubmitListener = listener;
    }

    private static class ViewHolder {
        private TextView newGoodID;
        private TextView oldGoodID;
        private TextView count;

        private Button submit;

        private ViewHolder(View root) {
            this.newGoodID = root.findViewById(R.id.newGoodsID);
            this.oldGoodID = root.findViewById(R.id.oldGoodsID);
            this.count = root.findViewById(R.id.count);
            this.submit = root.findViewById(R.id.submit);
        }
    }
}
