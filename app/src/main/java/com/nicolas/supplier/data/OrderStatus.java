package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

/**
 * 发货状态---库房发货状态(swait供货商待接单/swaited供货商已接单/roomreceive库房已收货/roomsend库房已发货/branchreceive分店已收货)
 */
public class OrderStatus implements Parcelable {

    public static final String NONE = "";
    public static final String SWAIT = "供货商待接单";
    public static final String SWAITED = "供货商已接单";
    public static final String ROOM_RECEIVE = "库房已收货";
    public static final String ROOM_SEND = "库房已发货";
    public static final String BRANCH_RECEIVE = "分店已收货";

    public static final String[] value = {
            SupplierApp.getInstance().getString(R.string.swait),
            SupplierApp.getInstance().getString(R.string.swaited),
            SupplierApp.getInstance().getString(R.string.roomreceive),
            SupplierApp.getInstance().getString(R.string.roomsend),
            SupplierApp.getInstance().getString(R.string.branchreceive)};

    private String status;      //订单状态

    public OrderStatus() {
        this.status = NONE;
    }

    public OrderStatus(String status) {
        this.status = status;
    }

    protected OrderStatus(Parcel in) {
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
        @Override
        public OrderStatus createFromParcel(Parcel in) {
            return new OrderStatus(in);
        }

        @Override
        public OrderStatus[] newArray(int size) {
            return new OrderStatus[size];
        }
    };

    public void updateStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getShowContent() {
        switch (this.status) {
            case "swait":
                return value[0];
            case "swaited":
                return value[1];
            case "roomreceive":
                return value[2];
            case "roomsend":
                return value[3];
            case "branchreceive":
                return value[4];
            default:
                return "";
        }
    }

    public static String[] getValues() {
        return value;
    }

    public void valueOf(String itemName) {
        if (itemName.equals(value[0])) {
            this.status = SWAIT;
        } else if (itemName.equals(value[1])) {
            this.status = SWAITED;
        } else if (itemName.equals(value[2])) {
            this.status = ROOM_RECEIVE;
        } else if (itemName.equals(value[3])) {
            this.status = ROOM_SEND;
        } else if (itemName.equals(BRANCH_RECEIVE)) {
            this.status = BRANCH_RECEIVE;
        } else {
            this.status = NONE;
        }
    }
}
