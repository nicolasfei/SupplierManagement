package com.nicolas.supplier.server;

import com.nicolas.supplier.supplier.SupplierKeeper;

import java.util.Map;

public class CommandVo {
    public String url;
    public String token;
    public Map<String, String> parameters;
    public String requestMode;
    public String contentType;
    public CommandTypeEnum typeEnum;

    public CommandVo() {
        this.token = SupplierKeeper.getInstance().getToken();
    }
}
