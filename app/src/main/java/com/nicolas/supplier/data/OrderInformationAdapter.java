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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;
import com.nicolas.toollibrary.Utils;

import java.util.List;

public class OrderInformationAdapter extends BaseAdapter {
    private static final String TAG = "OrderInformationAdapter";
    private List<OrderInformation> informationList;
    private Context context;
    private boolean isBusy = false;               //表示list view是否在快速滑动
    private boolean canSetNumColorSize = true;    //默认不能修改颜色尺码数量

    private OnOrderChangeListener orderChangeListener;

    //上一次点击时间---点击防抖
    private static long lastClickTime = 0;
    private static final int INTERVAL_TIME = 600;

    public OrderInformationAdapter(Context context, List<OrderInformation> list) {
        this.context = context;
        this.informationList = list;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void setCanSetNumColorSize(boolean canSetNumColorSize) {
        this.canSetNumColorSize = canSetNumColorSize;
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

        //编号
        String posValue = context.getString(R.string.pos) + context.getString(R.string.colon) + (position + 1);
        holder.pos.setText(posValue);
        //加载图片
        if (!isBusy) {
            ImageLoadClass.getInstance().displayImage(order.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(order.img, holder.photo, true);
        }

        //库房
        String warehouse = "<font color=\"black\"><big>" + order.storeRoomName + "</big></font>";
        holder.warehouse.setText(Html.fromHtml(warehouse, Html.FROM_HTML_MODE_COMPACT));

        //订单状态
        String inState;
        if (!(order.valid.equals(context.getString(R.string.inValid)))) {        //正常订单
            inState = "<font color=\"black\"><big>" + order.inState.getStatus() + "</big></font>";
            if (order.inState.getStatus().equals(OrderStatus.SWAIT)) {        //用户可以接单，可以作废订单
                holder.orderStatus.setBackgroundColor(Color.RED);
//                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, SupplierApp.getInstance().getDrawable(R.drawable.ic_sr_blue), null);
//                holder.orderStatus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
//                            if (orderChangeListener != null) {
//                                orderChangeListener.orderTakeAndInValidOperation(order.id);   //用户操作接单状态
//                            }
//                            lastClickTime = System.currentTimeMillis();
//                        }
//                    }
//                });
//                holder.orderStatus.setClickable(true);
            } else if (order.inState.getStatus().equals(OrderStatus.SWAITED)) {     //用户已经接单，可以作废订单
                holder.orderStatus.setBackgroundColor(Color.YELLOW);
//                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, SupplierApp.getInstance().getDrawable(R.drawable.ic_sr_blue), null);
//                holder.orderStatus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
//                            if (orderChangeListener != null) {
//                                orderChangeListener.orderInValidOperation(order.id);   //用户操作接单状态
//                            }
//                            lastClickTime = System.currentTimeMillis();
//                        }
//                    }
//                });
//                holder.orderStatus.setClickable(true);
            } else {
                holder.orderStatus.setBackgroundColor(Color.GREEN);
//                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
//                holder.orderStatus.setOnClickListener(null);
//                holder.orderStatus.setClickable(false);
            }
        } else {                                //作废订单
            inState = "<font color=\"black\"><big>" + order.valid + "</big></font>";
            holder.orderStatus.setBackgroundColor(Color.LTGRAY);
//            holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }
        holder.orderStatus.setText(Html.fromHtml(inState, Html.FROM_HTML_MODE_COMPACT));

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
                if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                    if (order.hasQueryProperties) {     //已经查询了属性
                        if (order.expansion) {       //展开状态，关闭
                            holder.goodsContent.setVisibility(View.GONE);
                            order.expansion = false;
                            holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
                        } else {
                            holder.goodsContent.setVisibility(View.VISIBLE);
                            order.expansion = true;
                            holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
                        }
                    } else {        //还未查属性，查询属性
                        if (orderChangeListener != null) {
                            orderChangeListener.orderPropertyQuery(order.id);
                        }
                    }
                    lastClickTime = System.currentTimeMillis();
                }
            }
        });

        String shop = context.getString(R.string.branch) + "：" + "<font color=\"red\">" + order.fId + "</font>";
        holder.shop.setText(Html.fromHtml(shop, Html.FROM_HTML_MODE_COMPACT));

        String orderID = context.getString(R.string.orderID) + "：" + "<font color=\"black\">" + order.id + "</font>";
        holder.orderID.setText(Html.fromHtml(orderID, Html.FROM_HTML_MODE_COMPACT));

        String orderClass = /*context.getString(R.string.order_class) + "：" + */"<font color=\"black\">" + order.orderType.getType() + "</font>";
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

        String remark = context.getString(R.string.remark) + "：" + order.remark;
        holder.remark.setText(Html.fromHtml(remark, Html.FROM_HTML_MODE_COMPACT));

        holder.choice.setOnCheckedChangeListener(null);
        holder.choice.setVisibility(order.canSelect ? View.VISIBLE : View.INVISIBLE);
        holder.choice.setChecked(order.select);
        holder.choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                    order.select = isChecked;
                    if (orderChangeListener != null) {
                        orderChangeListener.orderChoice(order);
                    }
//                    if (!order.hasQueryProperties) {
//                        if (orderChangeListener != null) {
//                            orderChangeListener.orderPropertyQuery(order.id);
//                        }
//                    }
                    lastClickTime = System.currentTimeMillis();
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
            TextView color = layout.findViewById(R.id.color);       //颜色
            color.setText(attr.actualColor);
            TextView size = layout.findViewById(R.id.size);         //尺码
            size.setText(attr.actualSize);
            final EditText numT = layout.findViewById(R.id.num);    //数量
            numT.setText(String.valueOf(attr.actualNum));
            ImageButton reduce = layout.findViewById(R.id.reduce);       //数量减
            ImageButton add = layout.findViewById(R.id.add);             //数量加
            //条件判断，修改颜色尺码
            if (canSetNumColorSize) {
                //发货数量的修改只能在：供应商待接单 和 供应商已接单 状态下才能修改
                if (order.inState.getStatus().equals(OrderStatus.SWAIT) || order.inState.getStatus().equals(OrderStatus.SWAITED)) {
                    numT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        /**
                         * 参数说明
                         *
                         * @param v        被监听的对象
                         * @param actionId 动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
                         * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
                         * @return 返回你的动作
                         */
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {      //下一个/回车键
                                    String value = numT.getText().toString();
                                    if (!TextUtils.isEmpty(value)) {
                                        int newNum = Integer.parseInt(value);
                                        if (newNum <= 0 || newNum > attr.val) {
                                            numT.setText(("" + attr.actualNum));
                                            Utils.toast(context, R.string.inputError_fix_oder_pnum);
                                        } else if (newNum == attr.actualNum) {
                                            //
                                        } else {
                                            if (orderChangeListener != null) {
                                                orderChangeListener.orderGoodsNumChange(order.id, attr.id, newNum);
                                            }
                                        }
                                    } else {
                                        numT.setText(("" + attr.actualNum));
                                        Utils.toast(context, R.string.inputError_fix_oder_pnum);
                                    }
                                }
                                lastClickTime = System.currentTimeMillis();
                            }
                            return false;
                        }
                    });
                    //监听数量减
                    reduce.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                                if (orderChangeListener != null) {
                                    orderChangeListener.orderGoodsNumChange(order.id, attr.id, attr.actualNum - 1);
                                }
                                lastClickTime = System.currentTimeMillis();
                            }
                        }
                    });
                    if (attr.actualNum == 0) {
                        reduce.setImageDrawable(context.getDrawable(R.drawable.ic_reduce_grey));
                        reduce.setClickable(false);
                    }
                    //监听数量加
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                                if (orderChangeListener != null) {
                                    orderChangeListener.orderGoodsNumChange(order.id, attr.id, attr.actualNum + 1);
                                }
                                lastClickTime = System.currentTimeMillis();
                            }
                        }
                    });
                    if (attr.actualNum == attr.val) {
                        add.setImageDrawable(context.getDrawable(R.drawable.ic_add_grey));
                        add.setClickable(false);
                    }
                } else {     //其他订单状态下不能修改数量
                    numT.setFocusable(false);
                    numT.setBackground(null);
                    add.setVisibility(View.INVISIBLE);
                    reduce.setVisibility(View.INVISIBLE);
                }
                if (order.valid.equals(OrderValid.INVALID)) {
                    numT.setFocusable(false);
                    numT.setBackground(null);
                    add.setVisibility(View.INVISIBLE);
                    reduce.setVisibility(View.INVISIBLE);
                }
            } else {
                numT.setFocusable(false);
                numT.setBackground(null);
                add.setVisibility(View.INVISIBLE);
                reduce.setVisibility(View.INVISIBLE);
            }
            holder.goodsContent.addView(layout);
        }

        //根据expansion决定展开或是关闭
        if (!order.expansion) {       //展开状态，关闭
            holder.goodsContent.setVisibility(View.GONE);
            holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
        } else {
            holder.goodsContent.setVisibility(View.VISIBLE);
            holder.sendAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
        }
        //加急订单背景色显著显示
        if (order.isUrgent != null) {
            if (order.isUrgent.equals(OrderUrgent.URGENT_URGENT)) {
                convertView.setBackgroundColor(Color.rgb(0x99, 0x32, 0xCD));        //紫色
            } else {
                convertView.setBackgroundColor(Color.rgb(0xFF, 0xFF, 0xFF));        //白色
            }
        }
        return convertView;
    }

    public void setOnOrderChangeListener(OnOrderChangeListener listener) {
        this.orderChangeListener = listener;
    }

    public interface OnOrderChangeListener {
        void orderChoice(OrderInformation information);         //订单被选中

        void orderGoodsNumChange(String orderID, String propertyRecordID, int num);     //订单货物数量改变

        void orderTakeAndInValidOperation(String orderID);      //用户可以接单或者作废订单

        void orderInValidOperation(String orderID);         //用户可以作废订单

        void orderPropertyQuery(String orderID);      //订单属性查询
    }

    private static class ViewHolder {
        private TextView pos;
        private ImageView photo;
        private TextView warehouse, orderTime, invalidTime, amount, sendAmount;
        private TextView shop, orderClass, goodsID, newGoodsID, orderID;
        private TextView inPrice, orderPrice, orderStatus, remark, isPrint;
        private CheckBox choice;
        private LinearLayout goodsContent;

        private ViewHolder(View rootView) {
            this.pos = rootView.findViewById(R.id.pos);
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
