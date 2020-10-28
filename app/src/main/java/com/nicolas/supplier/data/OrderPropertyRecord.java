package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderPropertyRecord implements Parcelable {

    public String id;       //"5555",
    public String color;    //"卡其色",
    public String size;     //"M",
    public int val;         //属性数量
    public int orderVal;    //订单数量

    public String actualColor;        //实际颜色
    public String actualSize;         //实际尺码
    public int actualNum;             //实际件数

    public OrderPropertyRecord() {
        this.id = "";
        this.color = "";
        this.size = "";
        this.val = 0;
        this.orderVal = 0;
        this.actualNum = this.val;
        this.actualColor = this.color;
        this.actualSize = this.size;
    }

    public OrderPropertyRecord(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.color = object.getString("color");
            this.size = object.getString("size");
            this.val = object.getInt("val");
            this.orderVal = object.getInt("orderVal");
            this.actualColor = this.color;
            this.actualSize = this.size;
            this.actualNum = this.val;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected OrderPropertyRecord(Parcel in) {
        id = in.readString();
        color = in.readString();
        size = in.readString();
        val = in.readInt();
        orderVal = in.readInt();
        actualColor = in.readString();
        actualSize = in.readString();
        actualNum = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(color);
        dest.writeString(size);
        dest.writeInt(val);
        dest.writeInt(orderVal);
        dest.writeString(actualColor);
        dest.writeString(actualSize);
        dest.writeInt(actualNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderPropertyRecord> CREATOR = new Creator<OrderPropertyRecord>() {
        @Override
        public OrderPropertyRecord createFromParcel(Parcel in) {
            return new OrderPropertyRecord(in);
        }

        @Override
        public OrderPropertyRecord[] newArray(int size) {
            return new OrderPropertyRecord[size];
        }
    };
}
