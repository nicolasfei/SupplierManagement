package com.nicolas.supplier.server.common;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class CommonInterface extends AbstractInterface {
    //查询商品类型
    public final static String GoodsClassQuery = AbstractInterface.COMMAND_URL + "Supplier/GoodsClass";
    //查询分店信息
    public final static String BranchQuery = AbstractInterface.COMMAND_URL + "Supplier/Branch";
    //查询库房信息
    public final static String StorehouseQuery = AbstractInterface.COMMAND_URL + "Supplier/StoreRoom";
    //货号类型
    public final static String GoodsType = AbstractInterface.COMMAND_URL + "Supplier/GoodsType";

    //版本监测
    public final static String VersionCheck = "http://updatesupplier.scdawn.com/v.json";
    //版本监测
    public final static String NoticeCheck = "http://updatesupplier.scdawn.com/g.json";

    //供货商货号销售统计
    public final static String StatisticsGoodsId = AbstractInterface.COMMAND_URL + "Supplier/StatisticsGoodsId";
    //供货商货号统计汇总
    public final static String StatisticsGoodsIdTotal = AbstractInterface.COMMAND_URL + "Supplier/StatisticsGoodsIdTotal";
}
