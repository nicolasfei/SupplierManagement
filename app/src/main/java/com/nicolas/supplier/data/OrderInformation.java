package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单信息类
 * "id;//"111",
 * "gId;//null,
 * "supplierId;//"9527",
 * "supplierName;//"丫头弟弟",
 * "goodsClassId;//"2",
 * "goodsClassName;//"男装",
 * "branchId;//"",
 * "branchName;//null,
 * "storeRoomId;//"A1103",
 * "storeRoomName;//"金牛之心1103",
 * "dearClassId;//null,
 * "dearClassName;//null,
 * "oldGoodsId;//"1213",
 * "goodsId;//"200710201",
 * "inPrice;//"30.00",
 * "originalPrice;//"30.00",
 * "orderPrice;//"30.00",
 * "amount;//20,
 * "sendAmount;//0,
 * "salePrice;//null,
 * "orderType;//"统下",
 * "createTime;//"2020-07-12 11:50",
 * "inValidTime;//"2020-07-16",
 * "isPrint;//"未打印",
 * "printTime;//"1900-01-01 00:00",
 * "inState;//"供货商待接单",
 * "roomSendTime;//"1900-01-01",
 * "branchReceiveTime;//"1900-01-01",
 * "sendId;//"",
 * "remark;//"",
 * "valid;//"启用",
 * "propertyRecord;//[
 * {
 * "id;//"4444",
 * "orderId;//"222",
 * "color;//"白色",
 * "size;//"S",
 * "val;//10
 * },
 * {
 * "id;//"5555",
 * "orderId;//"222",
 * "color;//"卡其色",
 * "size;//"M",
 * "val;//10
 * }
 * ]
 */
public class OrderInformation implements Parcelable {

    public static final String sort_order_time = SupplierApp.getInstance().getString(R.string.sort_order_time);
    public static final String sort_branch = SupplierApp.getInstance().getString(R.string.sort_branch);
    public static final String sort_warehouse = SupplierApp.getInstance().getString(R.string.sort_warehouse);
    public static final String sort_order_class = SupplierApp.getInstance().getString(R.string.sort_order_class);
    public static final String sort_order_num_rise = SupplierApp.getInstance().getString(R.string.sort_order_num_rise);
    public static final String sort_order_num_drop = SupplierApp.getInstance().getString(R.string.sort_order_num_drop);


    public boolean canChecked;       //是否显示可选择checkbox
    public boolean checked;         //是否被选中
    public boolean expansion;       //是否展开显示细节

    public String id;               //ID
    public String gId;              //货号ID
    public String supplierId;       //供货商主键编号
    public String supplierName;     //供货商名称
    public String goodsClassId;     //货物类别编号
    public String goodsClassName;   //货物类别名称--"男装",
    public String branchId;         //分店编号
    public String branchName;       //分店名称
    public String storeRoomId;      //库房编号--"A1103",
    public String storeRoomName;    //库房名称或地址--"金牛之心1103",
    public String dearClassId;      //null,
    public String dearClassName;    //null,
    public String oldGoodsId;       //旧货号--"1213",
    public String goodsId;          //新货号--"200710201",
    public float inPrice;           //进价--"30.00",
    public float originalPrice;     //原进价--"30.00",
    public float orderPrice;        //订货价--"30.00",
    public int amount;              //订单数量--20,订单数量
    public int sendAmount;          //实际发货数量--0,实际发货数量
    public String salePrice;        //null,
    public OrderClass orderType;    //订单类型--"统下",
    public String createTime;       //创建日期--"2020-07-12 11:50",
    public String inValidTime;      //订单过期时间--"2020-07-16",
    public PrintStatus isPrint;     //已打印/未打印--"未打印",
    public String printTime;        //打印时间--"1900-01-01 00:00",
    public String inState;          //"供货商待接单",
    public String roomSendTime;     //库房收货时间---"1900-01-01",
    public String branchReceiveTime;//分店接收时间---"1900-01-01",
    public String sendId;           //"",
    public String remark;           //备注"",
    public String valid;            //"启用",
    public String img;              //图片
    public List<OrderPropertyRecord> propertyRecords;


    public OrderInformation() {
        this.id = "0123456";               //ID
        this.gId = "0123456";              //货号ID
        this.supplierId = "0123456";       //供货商主键编号
        this.supplierName = "0123456";     //供货商名称
        this.goodsClassId = "0123456";     //货物类别编号
        this.goodsClassName = "0123456";   //货物类别名称--"男装",
        this.branchId = "0123456";         //分店编号
        this.branchName = "0123456";       //分店名称
        this.storeRoomId = "0123456";      //库房编号--"A1103",
        this.storeRoomName = "0123456";    //库房名称或地址--"金牛之心1103",
        this.dearClassId = "0123456";      //null,
        this.dearClassName = "0123456";    //null,
        this.oldGoodsId = "0123456";       //旧货号--"1213",
        this.goodsId = "0123456";          //新货号--"200710201",
        this.inPrice = 30;           //进价--"30.00",
        this.originalPrice = 30;     //原进价--"30.00",
        this.orderPrice = 30;        //订货价--"30.00",
        this.amount = 20;              //订单数量--20,订单数量
        this.sendAmount = 20;          //实际发货数量--0,实际发货数量
        this.salePrice = "0123456";        //null,
        this.orderType = new OrderClass("通下");    //订单类型--"统下",
        this.createTime = "2020-07-12 11:50";       //创建日期--"2020-07-12 11:50",
        this.inValidTime = "2020-07-16";      //订单过期时间--"2020-07-16",
        this.isPrint = new PrintStatus("未打印");     //已打印/未打印--"未打印",
        this.printTime = "2020-07-12 11:50";        //打印时间--"1900-01-01 00:00",
        this.inState = "供货商待接单";          //"供货商待接单",
        this.roomSendTime = "2020-07-16";     //库房收货时间---"1900-01-01",
        this.branchReceiveTime = "2020-07-16";//分店接收时间---"1900-01-01",
        this.sendId = "123456";           //"",
        this.remark = "123456";           //备注"",
        this.valid = "启用";            //"启用",
        this.img = "123456";              //图片
        this.propertyRecords = new ArrayList<>();
        this.propertyRecords.add(new OrderPropertyRecord());
        this.propertyRecords.add(new OrderPropertyRecord());
        this.propertyRecords.add(new OrderPropertyRecord());
        this.propertyRecords.add(new OrderPropertyRecord());
    }

    public OrderInformation(String json) {
        Log.d("tag", "OrderInformation: "+json);
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.gId = object.getString("gId");
            this.supplierId = object.getString("supplierId");
            this.supplierName = object.getString("supplierName");
            this.goodsClassId = object.getString("goodsClassId");
            this.goodsClassName = object.getString("goodsClassName");
            this.branchId = object.getString("branchId");
            this.branchName = object.getString("branchName");
            this.storeRoomId = object.getString("storeRoomId");
            this.storeRoomName = object.getString("storeRoomName");
            this.dearClassId = object.getString("dearClassId");
            this.dearClassName = object.getString("dearClassName");
            this.oldGoodsId = object.getString("oldGoodsId");
            this.goodsId = object.getString("goodsId");
            this.inPrice = Float.parseFloat(object.getString("inPrice"));
            this.originalPrice = Float.parseFloat(object.getString("originalPrice"));
            this.orderPrice = Float.parseFloat(object.getString("orderPrice"));
            this.amount = object.getInt("amount");
            this.sendAmount = object.getInt("sendAmount");
            this.salePrice = object.getString("salePrice");
            this.img = object.getString("img");
            this.orderType = new OrderClass(object.getString("orderType"));
            this.createTime = object.getString("createTime");
            this.inValidTime = object.getString("inValidTime");
            this.isPrint = new PrintStatus(object.getString("isPrint"));
            this.printTime = object.getString("printTime")+":00";
            this.inState = object.getString("inState");
            this.roomSendTime = object.getString("roomSendTime");
            this.branchReceiveTime = object.getString("branchReceiveTime");
            this.sendId = object.getString("sendId");
            this.remark = object.getString("remark");
            this.valid = object.getString("valid");
            this.propertyRecords = new ArrayList<>();
            JSONArray array = new JSONArray(object.getString("propertyRecord"));
            for (int i = 0; i < array.length(); i++) {
                this.propertyRecords.add(new OrderPropertyRecord(array.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected OrderInformation(Parcel in) {
        canChecked = in.readByte() != 0;
        checked = in.readByte() != 0;
        expansion = in.readByte() != 0;
        id = in.readString();
        gId = in.readString();
        supplierId = in.readString();
        supplierName = in.readString();
        goodsClassId = in.readString();
        goodsClassName = in.readString();
        branchId = in.readString();
        branchName = in.readString();
        storeRoomId = in.readString();
        storeRoomName = in.readString();
        dearClassId = in.readString();
        dearClassName = in.readString();
        oldGoodsId = in.readString();
        goodsId = in.readString();
        inPrice = in.readFloat();
        originalPrice = in.readFloat();
        orderPrice = in.readFloat();
        amount = in.readInt();
        sendAmount = in.readInt();
        salePrice = in.readString();
        orderType = in.readParcelable(OrderClass.class.getClassLoader());
        createTime = in.readString();
        inValidTime = in.readString();
        isPrint = in.readParcelable(PrintStatus.class.getClassLoader());
        printTime = in.readString();
        inState = in.readString();
        roomSendTime = in.readString();
        branchReceiveTime = in.readString();
        sendId = in.readString();
        remark = in.readString();
        valid = in.readString();
        img = in.readString();
        propertyRecords = in.createTypedArrayList(OrderPropertyRecord.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (canChecked ? 1 : 0));
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (expansion ? 1 : 0));
        dest.writeString(id);
        dest.writeString(gId);
        dest.writeString(supplierId);
        dest.writeString(supplierName);
        dest.writeString(goodsClassId);
        dest.writeString(goodsClassName);
        dest.writeString(branchId);
        dest.writeString(branchName);
        dest.writeString(storeRoomId);
        dest.writeString(storeRoomName);
        dest.writeString(dearClassId);
        dest.writeString(dearClassName);
        dest.writeString(oldGoodsId);
        dest.writeString(goodsId);
        dest.writeFloat(inPrice);
        dest.writeFloat(originalPrice);
        dest.writeFloat(orderPrice);
        dest.writeInt(amount);
        dest.writeInt(sendAmount);
        dest.writeString(salePrice);
        dest.writeParcelable(orderType, flags);
        dest.writeString(createTime);
        dest.writeString(inValidTime);
        dest.writeParcelable(isPrint, flags);
        dest.writeString(printTime);
        dest.writeString(inState);
        dest.writeString(roomSendTime);
        dest.writeString(branchReceiveTime);
        dest.writeString(sendId);
        dest.writeString(remark);
        dest.writeString(valid);
        dest.writeString(img);
        dest.writeTypedList(propertyRecords);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInformation> CREATOR = new Creator<OrderInformation>() {
        @Override
        public OrderInformation createFromParcel(Parcel in) {
            return new OrderInformation(in);
        }

        @Override
        public OrderInformation[] newArray(int size) {
            return new OrderInformation[size];
        }
    };
}
