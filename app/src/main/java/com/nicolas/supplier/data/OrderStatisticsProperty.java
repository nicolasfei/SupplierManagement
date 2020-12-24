package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderStatisticsProperty implements Parcelable {
    public String color;    //颜色
    public String size;     //尺码
    public int num;         //数量

    public OrderStatisticsProperty(String color, String size, int num) {
        this.color = color.trim();
        this.size = size.trim();
        this.num = num;
    }

    protected OrderStatisticsProperty(Parcel in) {
        color = in.readString();
        size = in.readString();
        num = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(color);
        dest.writeString(size);
        dest.writeInt(num);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderStatisticsProperty> CREATOR = new Creator<OrderStatisticsProperty>() {
        @Override
        public OrderStatisticsProperty createFromParcel(Parcel in) {
            return new OrderStatisticsProperty(in);
        }

        @Override
        public OrderStatisticsProperty[] newArray(int size) {
            return new OrderStatisticsProperty[size];
        }
    };
}
