package com.nicolas.supplier.data;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.toollibrary.ImageLoadClass;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.app.SupplierApp;

import java.util.List;

public class OrderInformationAdapter extends BaseAdapter {
    private static final String TAG = "OrderInformationAdapter";
    private List<OrderInformation> informationList;
    private Context context;

    private OnOrderChangeListener orderChangeListener;

    public OrderInformationAdapter(Context context, List<OrderInformation> list) {
        this.context = context;
        this.informationList = list;
    }

    @Override
    public int getCount() {
        return this.informationList == null ? 0 : this.informationList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.informationList == null ? null : this.informationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.order_information_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final OrderInformation order = informationList.get(position);

        //加载图片
        new ImageLoadClass(context, holder.photo, order.img).load();

        //库房
        String warehouse = "<font color=\"black\"><big>" + order.storeRoomName + "</big></font>";
        holder.warehouse.setText(Html.fromHtml(warehouse, Html.FROM_HTML_MODE_COMPACT));

        //订单状态+打印时间
        String inState = "<font color=\"black\"><big>" + order.inState + "</big></font>";
//        if (order.isPrint.getStatus().equals(PrintStatus.PRINT)) {
//            inState += "<br><font color=\"gray\">" + context.getString(R.string.print_time) + context.getString(R.string.colon) + order.printTime + "</font></br>";
//        }
        holder.orderStatus.setText(Html.fromHtml(inState, Html.FROM_HTML_MODE_COMPACT));
        if (!(order.inState.equals(OrderStatus.SWAIT))) {        //(SupplierApp.getInstance().getString(R.string.swait))
            holder.orderStatus.setBackgroundColor(Color.BLUE);
            holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            holder.orderStatus.setOnClickListener(null);
            holder.orderStatus.setClickable(false);
        } else {
            holder.orderStatus.setBackgroundColor(Color.RED);
            holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, SupplierApp.getInstance().getDrawable(R.drawable.ic_sr_blue), null);
            holder.orderStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderChangeListener != null) {
                        orderChangeListener.orderInStatsChange(order.id);   //用户手动接单
                    }
                }
            });
            holder.orderStatus.setClickable(true);
        }

        String orderTime = context.getString(R.string.order_time) + "：" + "<font color=\"black\">" + order.createTime + "</font>";
        holder.orderTime.setText(Html.fromHtml(orderTime, Html.FROM_HTML_MODE_COMPACT));

        String invalidTime = context.getString(R.string.invalid_time) + "：" + "<font color=\"black\">" + order.inValidTime + "</font>";
        holder.invalidTime.setText(Html.fromHtml(invalidTime, Html.FROM_HTML_MODE_COMPACT));

        String amount = context.getString(R.string.amount) + "：" + "<font color=\"red\">" + order.amount + "</font>";
        holder.amount.setText(Html.fromHtml(amount, Html.FROM_HTML_MODE_COMPACT));

        String sendAmount = context.getString(R.string.sendAmount) + "：" + "<font color=\"red\">" + order.sendAmount + "</font>";
        holder.sendAmount.setText(Html.fromHtml(sendAmount, Html.FROM_HTML_MODE_COMPACT));
        holder.sendAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.expansion) {       //展开状态，关闭
                    holder.goodsContent.setVisibility(View.GONE);
                    order.expansion = false;
                    holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
                } else {
                    holder.goodsContent.setVisibility(View.VISIBLE);
                    order.expansion = true;
                    holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
                }
            }
        });

        String shop = context.getString(R.string.branch) + "：" + "<font color=\"red\">" + order.branchId + "</font>";
        holder.shop.setText(Html.fromHtml(shop, Html.FROM_HTML_MODE_COMPACT));

        String orderID = context.getString(R.string.orderID) + "：" + "<font color=\"black\">" + order.id + "</font>";
        holder.orderID.setText(Html.fromHtml(orderID, Html.FROM_HTML_MODE_COMPACT));

        String orderClass = context.getString(R.string.order_class) + "：" + "<font color=\"black\">" + order.orderType.getType() + "</font>";
        holder.orderClass.setText(Html.fromHtml(orderClass, Html.FROM_HTML_MODE_COMPACT));

        String goodsID = context.getString(R.string.old_goods_id) + "：" + "<font color=\"black\">" + order.oldGoodsId + "</font>";
        holder.goodsID.setText(Html.fromHtml(goodsID, Html.FROM_HTML_MODE_COMPACT));

        String newGoodsID = context.getString(R.string.new_goods_id) + "：" + "<font color=\"black\">" + order.goodsId + "</font>";
        holder.newGoodsID.setText(Html.fromHtml(newGoodsID, Html.FROM_HTML_MODE_COMPACT));

        String inPrice = context.getString(R.string.inPrice) + "：" + context.getString(R.string.money) + "<font color=\"red\"><big>" + order.inPrice + "</big></font>";
        holder.inPrice.setText(Html.fromHtml(inPrice, Html.FROM_HTML_MODE_COMPACT));

        String orderPrice = context.getString(R.string.orderPrice) + "：" + context.getString(R.string.money) + "<font color=\"red\"><big>" + order.orderPrice + "</big></font>";
        holder.orderPrice.setText(Html.fromHtml(orderPrice, Html.FROM_HTML_MODE_COMPACT));

        String printTime;
        if (order.isPrint.getStatus().equals(PrintStatus.PRINT)) {
            printTime = context.getString(R.string.print_time) + "：" + "<font color=\"black\">" + order.printTime + "</font>";
        } else {
            printTime = context.getString(R.string.no_print);
        }
        holder.isPrint.setText(Html.fromHtml(printTime, Html.FROM_HTML_MODE_COMPACT));

//        String shipmentTime;
//        if (order.status.getType() == OrderStatus.SHIPMENT) {
//            shipmentTime = context.getString(R.string.shipment_time) + "：" + "<font color=\"blue\">" + order.shipmentTime + "</font>";
//        } else {
//            shipmentTime = "<font color=\"red\">" + order.status.toString() + "</font>";        //context.getString(R.string.no_shipment);
//        }
//        holder.orderStatus.setText(Html.fromHtml(shipmentTime, Html.FROM_HTML_MODE_COMPACT));

        String remark = context.getString(R.string.remark) + "：" + order.remark;
        holder.remark.setText(Html.fromHtml(remark, Html.FROM_HTML_MODE_COMPACT));

        holder.choice.setVisibility(order.canChecked ? View.VISIBLE : View.INVISIBLE);
        holder.choice.setChecked(order.checked);
        holder.choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                order.checked = isChecked;
                if (orderChangeListener != null) {
                    orderChangeListener.orderChoice(order);
                }
            }
        });

        //添加商品的属性-----颜色,尺码,数量
        if (holder.goodsContent.getChildCount() > 2) {      //先移除之前加载的数据
            for (int i = 2; i < holder.goodsContent.getChildCount(); ) {
                holder.goodsContent.removeViewAt(i);
            }
        }
        for (final OrderPropertyRecord attr : order.propertyRecords) {
            View layout = LayoutInflater.from(this.context).inflate(R.layout.order_goods_information_item, null, false);
            TextView color = layout.findViewById(R.id.color);
            color.setText(attr.actualColor);
//            color.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    attr.actualColor = s.toString();
//                }
//            });
            TextView size = layout.findViewById(R.id.size);
            size.setText(attr.actualSize);
//            size.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    attr.actualSize = s.toString();
//                }
//            });
            final EditText numT = layout.findViewById(R.id.num);
            numT.setText(("" + attr.actualNum));
            numT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                /**
                 * 参数说明
                 * @param v 被监听的对象
                 * @param actionId  动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
                 * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
                 * @return 返回你的动作
                 */
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {      //下一个/回车键
                        String value = numT.getText().toString();
                        if (!TextUtils.isEmpty(value)) {
                            int newNum = Integer.parseInt(value);
                            if (newNum <= 0 || newNum > attr.val) {
                                numT.setText(("" + attr.actualNum));
                                Utils.toast(context, R.string.inputError);
                            } else if (newNum == attr.actualNum) {
                                //
                            } else {
                                if (orderChangeListener != null) {
                                    orderChangeListener.orderGoodsNumChange(attr.id, newNum);
                                }
                            }
                        } else {
                            numT.setText(("" + attr.actualNum));
                            Utils.toast(context, R.string.inputError);
                        }
                    }
                    return false;
                }
            });
//            numT.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    String value = s.toString();
//                    if (!TextUtils.isEmpty(value)) {
//                        int newNum = Integer.parseInt(value);
//                        if (newNum <= 0 || newNum > attr.val) {
//                            numT.setText(("" + attr.actualNum));
//                            Utils.toast(context, R.string.inputError);
//                        } else {
//                            if (orderChangeListener != null) {
//                                orderChangeListener.orderGoodsNumChange(attr.id, newNum);
//                            }
//                        }
//                    } else {
//                        numT.setText(("" + attr.actualNum));
//                        Utils.toast(context, R.string.inputError);
//                    }
//                }
//            });
            Button reduce = layout.findViewById(R.id.reduce);
            Button add = layout.findViewById(R.id.add);
            reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    attr.actualNum--;
//                    order.sendAmount--;
//                    if (attr.actualNum == 0) {
//                        reduce.setBackground(context.getDrawable(R.drawable.ic_reduce_grey));
//                        reduce.setClickable(false);
//                    }
//                    if (attr.actualNum < attr.val && !add.isClickable()) {
//                        add.setBackground(context.getDrawable(R.drawable.button_add));
//                        add.setClickable(true);
//                    }
//                    numT.setText(("x" + attr.actualNum));
//                    String num = context.getString(R.string.num) + "：" + "<font color=\"red\">" + order.sendAmount + "</font>";
//                    holder.num.setText(Html.fromHtml(num, Html.FROM_HTML_MODE_COMPACT));
                    if (orderChangeListener != null) {
                        orderChangeListener.orderGoodsNumChange(attr.id, attr.actualNum - 1);
                    }
                }
            });
            if (attr.actualNum == 0) {
                reduce.setBackground(context.getDrawable(R.drawable.ic_reduce_grey));
                reduce.setClickable(false);
            }
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    attr.actualNum++;
//                    order.sendAmount++;
//                    if (attr.actualNum == attr.val) {
//                        add.setBackground(context.getDrawable(R.drawable.ic_add_grey));
//                        add.setClickable(false);
//                    }
//                    if (attr.actualNum > 0 && !reduce.isClickable()) {
//                        reduce.setBackground(context.getDrawable(R.drawable.button_reduce));
//                        reduce.setClickable(true);
//                    }
//                    numT.setText(("x" + attr.actualNum));
//                    String num = context.getString(R.string.num) + "：" + "<font color=\"red\">" + order.sendAmount + "</font>";
//                    holder.num.setText(Html.fromHtml(num, Html.FROM_HTML_MODE_COMPACT));
                    if (orderChangeListener != null) {
                        orderChangeListener.orderGoodsNumChange(attr.id, attr.actualNum + 1);
                    }
                }
            });
            if (attr.actualNum == attr.val) {
                add.setBackground(context.getDrawable(R.drawable.ic_add_grey));
                add.setClickable(false);
            }
            holder.goodsContent.addView(layout);
        }

        holder.goodsContent.setVisibility(order.expansion ? View.VISIBLE : View.GONE);

        return convertView;
    }

    public void setOnOrderChangeListener(OnOrderChangeListener listener) {
        this.orderChangeListener = listener;
    }

    public interface OnOrderChangeListener {
        void orderChoice(OrderInformation information);

        void orderGoodsNumChange(String propertyRecordID, int num);

        void orderInStatsChange(String orderID);
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView warehouse, orderTime, invalidTime, amount, sendAmount;
        private TextView shop, orderClass, goodsID, newGoodsID, orderID;
        private TextView inPrice, orderPrice, orderStatus, remark, isPrint;
        private CheckBox choice;
        private LinearLayout goodsContent;

        private ViewHolder(View rootView) {
            this.photo = rootView.findViewById(R.id.photo);
            this.warehouse = rootView.findViewById(R.id.warehouse);
            this.orderTime = rootView.findViewById(R.id.orderTime);
            this.invalidTime = rootView.findViewById(R.id.invalidTime);
            this.amount = rootView.findViewById(R.id.amount);
            this.sendAmount = rootView.findViewById(R.id.sendAmount);

            this.shop = rootView.findViewById(R.id.shop);
            this.orderClass = rootView.findViewById(R.id.orderClass);
            this.goodsID = rootView.findViewById(R.id.goodsID);
            this.newGoodsID = rootView.findViewById(R.id.newGoodsID);

            this.inPrice = rootView.findViewById(R.id.inPrice);
            this.orderPrice = rootView.findViewById(R.id.orderPrice);
            this.orderStatus = rootView.findViewById(R.id.orderStatus);
            this.remark = rootView.findViewById(R.id.remark);

            this.orderID = rootView.findViewById(R.id.orderID);
            this.isPrint = rootView.findViewById(R.id.isPrint);
            this.choice = rootView.findViewById(R.id.checkBox);
            this.goodsContent = rootView.findViewById(R.id.goodsContent);
        }
    }
}
