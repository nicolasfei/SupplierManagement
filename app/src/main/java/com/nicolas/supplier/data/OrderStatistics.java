package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OrderStatistics implements Parcelable {
    public String goodsID;
    public String oldGoodsID;
    public List<OrderStatisticsProperty> properties;

    public OrderStatistics(String goodsID, String oldGoodsID) {
        this.goodsID = goodsID;
        this.oldGoodsID = oldGoodsID;
        this.properties = new ArrayList<>();
    }

    protected OrderStatistics(Parcel in) {
        goodsID = in.readString();
        oldGoodsID = in.readString();
        properties = in.createTypedArrayList(OrderStatisticsProperty.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsID);
        dest.writeString(oldGoodsID);
        dest.writeTypedList(properties);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderStatistics> CREATOR = new Creator<OrderStatistics>() {
        @Override
        public OrderStatistics createFromParcel(Parcel in) {
            return new OrderStatistics(in);
        }

        @Override
        public OrderStatistics[] newArray(int size) {
            return new OrderStatistics[size];
        }
    };
}
