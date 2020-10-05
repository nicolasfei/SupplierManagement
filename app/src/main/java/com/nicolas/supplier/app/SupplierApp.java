package com.nicolas.supplier.app;

import android.app.Application;

import com.nicolas.toollibrary.GlobalCrashHandler;
import com.nicolas.supplier.FirstActivity;

public class SupplierApp extends Application {

    private static SupplierApp app;

    public static SupplierApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //初始化异常处理
        GlobalCrashHandler.getInstance().init(app, "",
                "fshq_test@163.com", "ZIPGXOJDAEZVOJBY", "fshq_debug@163.com",
                "She bangs supplierManagement", FirstActivity.class);
    }
}
