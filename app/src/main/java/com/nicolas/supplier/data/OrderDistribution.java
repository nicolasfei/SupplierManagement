package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 订单配送顺序
 */
public class OrderDistribution implements Parcelable {
    public String goodsId;
    public String distribution;

    public OrderDistribution(String distribution, String goodsId) {
        this.goodsId = goodsId;
        this.distribution = distribution;
    }

    protected OrderDistribution(Parcel in) {
        goodsId = in.readString();
        distribution = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsId);
        dest.writeString(distribution);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDistribution> CREATOR = new Creator<OrderDistribution>() {
        @Override
        public OrderDistribution createFromParcel(Parcel in) {
            return new OrderDistribution(in);
        }

        @Override
        public OrderDistribution[] newArray(int size) {
            return new OrderDistribution[size];
        }
    };
}
