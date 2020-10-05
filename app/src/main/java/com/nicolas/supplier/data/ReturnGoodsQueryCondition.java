package com.nicolas.supplier.data;

import com.nicolas.toollibrary.Tool;

public class ReturnGoodsQueryCondition {
    public String goodsClassId;
    public String oldGoodsId;
    public String fId;
    public String goodsId;
    public String barcodeID;        //选填    条码
    public String checkTime;        //默认查询近三天的

    public ReturnGoodsQueryCondition() {
        this.goodsClassId = "";
        this.oldGoodsId = "";
        this.fId = "";
        this.goodsId = "";
        this.barcodeID = "";
        this.checkTime = Tool.getNearlyThreeDaysDateSlot();
    }

    public void clear() {
        this.goodsClassId = "";
        this.oldGoodsId = "";
        this.fId = "";
        this.goodsId = "";
        this.barcodeID = "";
        this.checkTime = Tool.getNearlyThreeDaysDateSlot();
    }
}
