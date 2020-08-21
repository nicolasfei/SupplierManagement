package com.nicolas.supplier.server;

public enum CommandTypeEnum {
    COMMAND_SUPPLIER_LOGIN,       //供应商登陆
    COMMAND_SUPPLIER_ORDER,       //供应商订单
    COMMAND_SUPPLIER_GOODS_ID,    //供应商货号
    COMMAND_COMMON,               //通用功能
    COMMAND_SUPPLIER_MANAGEMENT,   //供应商管理
    COMMAND_SUPPLIER_SCORE_RECORD  //信誉分
}
