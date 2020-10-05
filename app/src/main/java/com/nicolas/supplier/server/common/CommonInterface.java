package com.nicolas.supplier.server.common;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class CommonInterface extends AbstractInterface {
    //查询商品类型
    public final static String GoodsClassQuery = AbstractInterface.COMMAND_URL + "Supplier/GoodsClass";
    //查询分店信息
    public final static String BranchQuery = AbstractInterface.COMMAND_URL + "Supplier/Branch";
    //查询库房信息
    public final static String StorehouseQuery = AbstractInterface.COMMAND_URL + "Supplier/StoreRoom";

    //版本监测
    public final static String VersionCheck = AbstractInterface.COMMAND_URL + "v.json";
}
