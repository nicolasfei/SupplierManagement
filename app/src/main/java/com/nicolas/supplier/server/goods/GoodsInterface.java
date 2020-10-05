package com.nicolas.supplier.server.goods;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class GoodsInterface extends AbstractInterface {
    //货号查询接口
    public final static String GoodsIDQuery = AbstractInterface.COMMAND_URL + "Supplier/GoodsId";
    //货号属性查询接口
    public final static String GoodsPropertyQuery = AbstractInterface.COMMAND_URL + "Supplier/GoodsIdByProperty";
    //货号设置无货接口
    public final static String GoodsStock = AbstractInterface.COMMAND_URL + "Supplier/GoodsIdIsStock";
    //货号属性设置无货接口
    public final static String GoodsPropertyStock = AbstractInterface.COMMAND_URL + "Supplier/GoodsIdPropertyIsStock";

    //返货查询接口
    public final static String ReturnGoodsQuery = AbstractInterface.COMMAND_URL + "Supplier/BackGoods";
}
