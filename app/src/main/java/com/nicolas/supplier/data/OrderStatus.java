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

    public static final String REQUEST_SWAIT = "swait";
    public static final String REQUEST_SWAITED = "swaited";
    public static final String REQUEST_ROOM_RECEIVE = "roomreceive";
    public static final String REQUEST_ROOM_SEND = "roomsend";
    public static final String REQUEST_BRANCH_RECEIVE = "branchreceive";

    public static final int NONE_ID = 0;
    public static final int SWAIT_ID = 1;
    public static final int SWAITED_ID = 2;
    public static final int ROOM_RECEIVE_ID = 3;
    public static final int ROOM_SEND_ID = 4;
    public static final int BRANCH_RECEIVE_ID = 5;
    public static final int INVALID_ID = 6;             //订单作废

    private String status;              //订单状态
    private String requestStatus;
    private int statusID = SWAIT_ID;    //订单状态ID，用于排序

    public static final String[] value = {
            SupplierApp.getInstance().getString(R.string.swait),
            SupplierApp.getInstance().getString(R.string.swaited),
            SupplierApp.getInstance().getString(R.string.roomreceive),
            SupplierApp.getInstance().getString(R.string.roomsend),
            SupplierApp.getInstance().getString(R.string.branchreceive)};

    public OrderStatus(String status) {
        this.status = status;
        switch (this.status) {
            case NONE:
                this.statusID = NONE_ID;
                this.requestStatus = NONE;
                break;
            case SWAIT:
                this.statusID = SWAIT_ID;
                this.requestStatus = REQUEST_SWAIT;
                break;
            case SWAITED:
                this.statusID = SWAITED_ID;
                this.requestStatus = REQUEST_SWAITED;
                break;
            case ROOM_RECEIVE:
                this.statusID = ROOM_RECEIVE_ID;
                this.requestStatus = REQUEST_ROOM_RECEIVE;
                break;
            case ROOM_SEND:
                this.statusID = ROOM_SEND_ID;
                this.requestStatus = REQUEST_ROOM_SEND;
                break;
            case BRANCH_RECEIVE:
                this.statusID = BRANCH_RECEIVE_ID;
                this.requestStatus = REQUEST_BRANCH_RECEIVE;
                break;
            default:
                break;
        }
    }

    public void updateStatus(String status) {
        if (this.status.equals(status)) {
            return;
        }
        this.status = status;
        switch (this.status) {
            case NONE:
                this.statusID = NONE_ID;
                this.requestStatus = NONE;
                break;
            case SWAIT:
                this.statusID = SWAIT_ID;
                this.requestStatus = REQUEST_SWAIT;
                break;
            case SWAITED:
                this.statusID = SWAITED_ID;
                this.requestStatus = REQUEST_SWAITED;
                break;
            case ROOM_RECEIVE:
                this.statusID = ROOM_RECEIVE_ID;
                this.requestStatus = REQUEST_ROOM_RECEIVE;
                break;
            case ROOM_SEND:
                this.statusID = ROOM_SEND_ID;
                this.requestStatus = REQUEST_ROOM_SEND;
                break;
            case BRANCH_RECEIVE:
                this.statusID = BRANCH_RECEIVE_ID;
                this.requestStatus = REQUEST_BRANCH_RECEIVE;
                break;
            default:
                break;
        }
    }

    public String getStatus() {
        return status;
    }

    public int getStatusID() {
        return statusID;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public static String[] getValues() {
        return value;
    }

    //--------------------Parcelable------------------//
    protected OrderStatus(Parcel in) {
        status = in.readString();
        requestStatus = in.readString();
        statusID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(requestStatus);
        dest.writeInt(statusID);
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
}
