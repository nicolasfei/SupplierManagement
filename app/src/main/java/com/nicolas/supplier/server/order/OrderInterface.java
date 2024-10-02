package com.nicolas.supplier.server.order;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class OrderInterface extends AbstractInterface {
    //订单查询接口
    public final static String GoodsOrder = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrder";
    //订单属性查询接口
    public final static String GoodsPropertyOrder = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderByProperty";
    //订单接单---这里是订单勾选打印
    public final static String GoodsOrderPrint = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderPrint";
    //订单接单---这里是打印所有订单
    public final static String GoodsOrderPrintAll = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderPrintAll";
    //订单接单---更新数量
    public final static String GoodsOrderVal = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderVal";
    //订单作废
    public final static String GoodsOrderInValid = AbstractInterface.COMMAND_URL+"Supplier/GoodsOrderInValid";
    //订单配送顺序
    public final static String GoodsOrderDistribution = AbstractInterface.COMMAND_URL+"Supplier/GoodsIdDeliverySortByGoodsId";
    //订单货号确认数据查询接口
    public final static String GoodsOrderID = AbstractInterface.COMMAND_URL+"Supplier/StatisticsSwaitOrder";
    //订单货号确认接口
    public final static String GoodsOrderSwaited = AbstractInterface.COMMAND_URL+"Supplier/OrderSwaited";
}
