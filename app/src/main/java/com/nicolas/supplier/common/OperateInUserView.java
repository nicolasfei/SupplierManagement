package com.nicolas.supplier.common;

import android.os.Message;

/**
 * Class exposing authenticated user details to the UI.
 */
public class OperateInUserView {
    private Message msg;
    //... other data fields that may be accessible to the UI

    public OperateInUserView(Message message) {
        this.msg = message;
    }

    public Message getMessage() {
        return msg;
    }
}
