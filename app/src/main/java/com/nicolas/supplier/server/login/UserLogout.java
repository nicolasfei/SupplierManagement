package com.nicolas.supplier.server.login;

import com.nicolas.supplier.server.CommandVo;

public class UserLogout extends LoginInterface {
    @Override
    public String getUrlParam() {
        return Logout;
    }
}
