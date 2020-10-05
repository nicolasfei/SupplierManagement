package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

/**
 * 订单类型---从服务器接收到的是value值：通下，首单，补货
 */
public class OrderClass implements Parcelable {
    public static final String ALL = "all";         //通单
    public static final String FIRST = "first";     //首单
    public static final String CPFR = "repair";     //补货
    public static final String NONE = "";

    private static final String[] value = {
            SupplierApp.getInstance().getString(R.string.order_all),
            SupplierApp.getInstance().getString(R.string.order_first),
            SupplierApp.getInstance().getString(R.string.order_CPFR)
    };

    private String type;        //订单类型

    public OrderClass(String type) {
        this.type = type;
    }

    /**
     * 获取订单类型
     *
     * @return 订单类型
     */
    public String getType() {
        return type;
    }

    /**
     * 更新订单类型
     *
     * @param type 订单类型
     */
    public void updateStatus(String type) {
        this.type = type;
    }

    /**
     * 获取用户查询条件所需的值
     *
     * @return 查询条件所需的值
     */
    public String getQueryField() {
        if (this.type.equals(NONE)) {
            return NONE;
        } else if (this.type.equals(value[0])) {
            return ALL;
        } else if (this.type.equals(value[1])) {
            return FIRST;
        } else if (this.type.equals(value[2])) {
            return CPFR;
        } else {
            return NONE;
        }
    }

    //------------------------Parcelable-----------------//
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
}
