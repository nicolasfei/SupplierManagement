package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

/**
 * 货号类型
 */
public class GoodsCodeClass implements Parcelable {
    public static final String Normal = "normal";       //正常
    public static final String Attempt = "attempt";     //试卖
    public static final String Replace = "replace";     //代卖
    public static final String Special = "special";     //特殊需求

    private static final String[] value = {
            SupplierApp.getInstance().getString(R.string.normal),
            SupplierApp.getInstance().getString(R.string.attempt),
            SupplierApp.getInstance().getString(R.string.replace),
            SupplierApp.getInstance().getString(R.string.special)};

    private String type;

    public GoodsCodeClass() {
        this.type = Normal;
    }

    public GoodsCodeClass(String type) {
        this.type = type;
    }

    protected GoodsCodeClass(Parcel in) {
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

    public static final Creator<GoodsCodeClass> CREATOR = new Creator<GoodsCodeClass>() {
        @Override
        public GoodsCodeClass createFromParcel(Parcel in) {
            return new GoodsCodeClass(in);
        }

        @Override
        public GoodsCodeClass[] newArray(int size) {
            return new GoodsCodeClass[size];
        }
    };

    public String getType() {
        return type;
    }
}
