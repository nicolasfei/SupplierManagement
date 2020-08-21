package com.nicolas.supplier.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

public class PrintStatus implements Parcelable {
    public static final String PRINT = "已打印";      //已打印
    public static final String UN_PRINT = "未打印";   //未打印
    public static final String NONE = "";

    private String status;

    private final static String[] value = {SupplierApp.getInstance().getString(R.string.printed), SupplierApp.getInstance().getString(R.string.no_print)};

    public PrintStatus() {
        this.status = NONE;
    }

    public PrintStatus(String status) {
        this.status = status;
    }

    protected PrintStatus(Parcel in) {
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

    public static final Creator<PrintStatus> CREATOR = new Creator<PrintStatus>() {
        @Override
        public PrintStatus createFromParcel(Parcel in) {
            return new PrintStatus(in);
        }

        @Override
        public PrintStatus[] newArray(int size) {
            return new PrintStatus[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public static String[] getValues() {
        return value;
    }
}
