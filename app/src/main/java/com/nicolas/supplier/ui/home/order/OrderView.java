package com.nicolas.supplier.ui.home.order;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nicolas.supplier.R;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.toollibrary.dcode.DCode;

public class OrderView extends LinearLayout {

    private Context mContext;
    private TextView printID;       //打印编号
    private TextView branchId;      //分店编号
    private TextView orderClass;    //订单类型--首单，补货。。。
    private ImageView code;         //订单条码

    private TextView supplier;      //供应商
    private TextView newGoodsID;    //新货号
    private TextView oldGoodsID;    //旧货号
    private TextView warehouse;     //库房

    private TextView goodsType;     //货物类别
    private TextView createTime;    //下单时间
    private TextView deadline;      //截至时间
    private TextView sendAmount;    //发货数量
    private TextView remark;        //备注

    private ListView listView;      //颜色尺码

    public OrderView(Context context, OrderInformation order, int printNum) {
        super(context);
        this.mContext = context;
        initView();
        initData(order, printNum);
    }

    private void initView() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.order_view, this, true);

        this.printID = root.findViewById(R.id.printID);
        this.branchId = root.findViewById(R.id.branchId);
        this.orderClass = root.findViewById(R.id.orderClass);
        this.code = root.findViewById(R.id.code);

        this.supplier = root.findViewById(R.id.supplier);
        this.newGoodsID = root.findViewById(R.id.newGoodsID);
        this.oldGoodsID = root.findViewById(R.id.oldGoodsID);
        this.warehouse = root.findViewById(R.id.warehouse);

        this.goodsType = root.findViewById(R.id.goodsType);
        this.createTime = root.findViewById(R.id.createTime);
        this.deadline = root.findViewById(R.id.deadline);
        this.sendAmount = root.findViewById(R.id.sendAmount);

        this.remark = root.findViewById(R.id.remark);
        this.listView = root.findViewById(R.id.listView);
    }

    private void initData(OrderInformation order, int printNum) {
        this.printID.setText(("编号：" + printNum));
        this.branchId.setText(order.fId);
        this.orderClass.setText(order.orderType.getType());

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.code.measure(width, height);
        int codeHeight = this.code.getMeasuredHeight();
        int codeWidth = this.code.getMeasuredWidth();
        Log.d("xxxxx------------>", "initData: " + codeHeight + "-->" + codeWidth);
//        this.code.setImageBitmap(DCode.createBarcode(order.id, codeWidth, codeHeight));//112, 56));
        this.code.setImageBitmap(DCode.createBarcode(order.id, 112, 56));

        String supplierValue = "供应商" + "\u3000" + order.sId;
        this.supplier.setText(Html.fromHtml(supplierValue, Html.FROM_HTML_MODE_COMPACT));

        String newGoodsIDValue = "新货号" + "\u3000" + order.goodsId;
        this.newGoodsID.setText(Html.fromHtml(newGoodsIDValue, Html.FROM_HTML_MODE_COMPACT));

        String oldGoodsIDValue = "旧货号" + "\u3000" + order.oldGoodsId;
        this.oldGoodsID.setText(Html.fromHtml(oldGoodsIDValue, Html.FROM_HTML_MODE_COMPACT));

        String warehouseValue = "\u3000库房" + "\u3000" + order.storeRoomName;
        this.warehouse.setText(Html.fromHtml(warehouseValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsTypeValue = "类别" + "\u3000" + order.goodsClassName;
        this.goodsType.setText(Html.fromHtml(goodsTypeValue, Html.FROM_HTML_MODE_COMPACT));

        String createTimeValue = "下单日期" + "\u3000" + order.createTime.substring(0, 10);
        this.createTime.setText(Html.fromHtml(createTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String deadlineValue = "截至日期" + "\u3000" + order.inValidTime;
        this.deadline.setText(Html.fromHtml(deadlineValue, Html.FROM_HTML_MODE_COMPACT));

        String sendAmountValue = "发货数量" + "\u3000" + "<font color=\"black\"><big>" + order.sendAmount + "</big></font>";
        this.sendAmount.setText(Html.fromHtml(sendAmountValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = "" + "备注" + "\u3000" + order.remark;
        this.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));

        OrderViewItemAdapter adapter = new OrderViewItemAdapter(this.mContext, order.propertyRecords);
        this.listView.setAdapter(adapter);
    }
}
