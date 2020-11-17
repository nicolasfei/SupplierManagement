package com.nicolas.supplier.ui.home.salestatistics;

public class SaleStatisticsQueryCondition {
    public String queryTime;                //查询时间
    public String newGoodsCode;             //新货号
    public String goodsType;                //货号类型

    private boolean isQueryConditionUpdate = false; //查询条件是否更新

    public SaleStatisticsQueryCondition() {
        this.queryTime = "";                //默认查询全部
        this.newGoodsCode = "";
        this.goodsType = "";
    }

    public void setNewGoodsCode(String newGoodsCode) {
        if (!newGoodsCode.equals(this.newGoodsCode)) {
            this.newGoodsCode = newGoodsCode;
            this.isQueryConditionUpdate = true;
        }
    }

    public void setGoodsType(String goodsType) {
        if (!goodsType.equals(this.goodsType)) {
            this.goodsType = goodsType;
            this.isQueryConditionUpdate = true;
        }
    }

    public void setQueryTime(String queryTime) {
        if (!queryTime.equals(this.queryTime)) {
            this.queryTime = queryTime;
            this.isQueryConditionUpdate = true;
        }
    }

    public void clear() {
        this.setNewGoodsCode("");
        this.setGoodsType("");
        this.setQueryTime("");
    }

    public boolean isQueryConditionUpdate() {
        return isQueryConditionUpdate;
    }

    public void resetQueryConditionUpdateStatus() {
        this.isQueryConditionUpdate = false;
    }
}
