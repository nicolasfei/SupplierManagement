package com.nicolas.supplier.server.order;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class OrderInterface extends AbstractInterface {
    //订单查询接口
    public final static String GoodsOrder = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrder";
    //订单接单---这里是订单打印
    public final static String GoodsOrderPrint = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderPrint";
    //订单接单---更新数量
    public final static String GoodsOrderVal = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderVal";
}
