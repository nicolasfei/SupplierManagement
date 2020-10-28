package com.nicolas.supplier.server.management;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class ManagementInterface extends AbstractInterface {
    //用户信息查询接口
    public final static String UserInformationQuery = AbstractInterface.COMMAND_URL + "api/manager/query";
    //用户管理-增
    public final static String UserHandlerAdd = AbstractInterface.COMMAND_URL + "api/manager/add";
    //用户管理-删
    public final static String UserHandlerDel = AbstractInterface.COMMAND_URL + "api/manager/del";
    //用户管理-查
    public final static String UserHandlerUpdate = AbstractInterface.COMMAND_URL + "api/manager/update";
    //用户管理-查
    public final static String UserHandlerQuery = AbstractInterface.COMMAND_URL + "api/manager/query";


    //供应商信息接口
    public final static String SupplierInformation = AbstractInterface.COMMAND_URL + "Supplier/SupplierById";
    //供应商账号信息接口
    public final static String SupplierAccountInformation = AbstractInterface.COMMAND_URL + "Supplier/SupplierAccountById";
    //用户管理-登陆记录查询
    public final static String SupplierLoginQuery = AbstractInterface.COMMAND_URL + "Supplier/SupplierAccountLoginRecord";
    //用户管理-供应商密码修改
    public final static String SupplierPassModify = AbstractInterface.COMMAND_URL + "Supplier/EditPassword";
    //用户管理-供应商密码修改--获取验证码
    public final static String SupplierGetVerificationCode = AbstractInterface.COMMAND_URL + "Supplier/SendSms";
}
