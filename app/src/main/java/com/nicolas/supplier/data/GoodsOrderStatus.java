package com.nicolas.supplier.data;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;

public class GoodsOrderStatus {
    public static final String Allow = "allow";
    public static final String Forbid = "forbid";

    private static final String[] value = {
            SupplierApp.getInstance().getString(R.string.allow),
            SupplierApp.getInstance().getString(R.string.forbid)};

    private String status;

    public GoodsOrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static String[] getValue() {
        return value;
    }
}
