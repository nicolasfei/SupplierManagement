package com.nicolas.supplier.server.login;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class LoginInterface extends AbstractInterface {
    //登陆接口
    public final static String Login = AbstractInterface.COMMAND_URL + "Supplier/Login";
    //登出接口
    public final static String Logout = AbstractInterface.COMMAND_URL + "Supplier/LoginOut";
}
