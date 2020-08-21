package com.nicolas.supplier.data;

/**
 * 货号查询，查询条件
 */
public class GoodsCodeQueryCondition {
    private String goodsClassId;     //选填    货物类别ID
    private String oldGoodsId;       //选填    旧货号
    private String goodsType;        //选填    货号类型(normal正常/attempt试卖/replace代卖/special特殊需求)
    private String isStock;          //选填    允许下单(allow允许/forbid禁止)
    private String createTime;       //选填    货号创建时间，默认当天    2019-01-01~2020-08-01
    private String goodsId;          //选填    生成的货号

    private boolean isGoodsCodeQueryConditionUpdate = false;    //

    public GoodsCodeQueryCondition() {
        this.goodsClassId = "";
        this.oldGoodsId = "";
        this.goodsType = "";

        this.isStock = "";
        this.createTime = "";
        this.goodsId = "";
    }

    public void clear() {
        this.setCreateTime("");
        this.setGoodsClassId("");
        this.setGoodsId("");

        this.setGoodsType("");
        this.setIsStock("");
        this.setOldGoodsId("");
    }

    public void setGoodsClassId(String goodsClassId) {
        if (!this.goodsClassId.equals(goodsClassId)) {
            this.goodsClassId = goodsClassId;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getGoodsClassId() {
        return goodsClassId;
    }

    public void setOldGoodsId(String oldGoodsId) {
        if (!this.oldGoodsId.equals(oldGoodsId)) {
            this.oldGoodsId = oldGoodsId;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getOldGoodsId() {
        return oldGoodsId;
    }

    public void setGoodsType(String goodsType) {
        if (!this.goodsType.equals(goodsType)) {
            this.goodsType = goodsType;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setCreateTime(String createTime) {
        if (!this.createTime.equals(createTime)) {
            this.createTime = createTime;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setIsStock(String isStock) {
        if (!this.isStock.equals(isStock)) {
            this.isStock = isStock;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getIsStock() {
        return isStock;
    }

    public void setGoodsId(String goodsId) {
        if (!this.goodsId.equals(goodsId)) {
            this.goodsId = goodsId;
            this.isGoodsCodeQueryConditionUpdate = true;
        }
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public boolean isQueryConditionUpdate() {
        return this.isGoodsCodeQueryConditionUpdate;
    }


    public void resetQueryConditionUpdateStatus() {
        this.isGoodsCodeQueryConditionUpdate = false;
    }
}
