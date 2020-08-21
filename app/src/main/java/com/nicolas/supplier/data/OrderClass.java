package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

/**
 * 订单类型
 */
public class OrderClass implements Parcelable {
    public static final String ALL = "all";         //通单
    public static final String FIRST = "first";     //首单
    public static final String CPFR = "repair";     //补货
    public static final String NONE = "";

    private static final String[] value = {
            SupplierApp.getInstance().getString(R.string.order_all),
            SupplierApp.getInstance().getString(R.string.order_first),
            SupplierApp.getInstance().getString(R.string.order_CPFR)};

    private String type;

    public OrderClass() {
        this.type = NONE;
    }

    public OrderClass(String type) {
        this.type = type;
    }

    protected OrderClass(Parcel in) {
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderClass> CREATOR = new Creator<OrderClass>() {
        @Override
        public OrderClass createFromParcel(Parcel in) {
            return new OrderClass(in);
        }

        @Override
        public OrderClass[] newArray(int size) {
            return new OrderClass[size];
        }
    };

    public String getType() {
        return type;
    }

    public String getShowName(){
        switch (type){
            case ALL:
                return value[0];
            case FIRST:
                return value[1];
            case CPFR:
                return value[2];
            default:
                return "";
        }
    }

    public static String[] getValues() {
        return value;
    }

    public void updateStatus(String type) {
        this.type = type;
    }
}
