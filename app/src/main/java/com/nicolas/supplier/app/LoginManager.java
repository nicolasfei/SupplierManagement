package com.nicolas.supplier.app;

import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.Utils;

public class LoginManager {
    private static LoginManager manager = new LoginManager();

    private LoginManager() {
    }

    public static LoginManager getInstance() {
        return manager;
    }

    /**
     * 登陆过期
     */
    public void loginExpire(String tips) {
        AppActivityManager.getInstance().popToFirstActivity();
        Utils.toast(SupplierApp.getInstance(), tips);
    }
}
