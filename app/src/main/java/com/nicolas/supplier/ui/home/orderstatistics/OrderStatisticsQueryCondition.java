package com.nicolas.supplier.ui.home.orderstatistics;

import com.nicolas.toollibrary.Tool;

public class OrderStatisticsQueryCondition {
    public String queryTime;                //查询时间
    public String newGoodsCode;             //新货号
    public String oldGoodsCode;             //旧货号

    private boolean isQueryConditionUpdate = false; //查询条件是否更新

    public OrderStatisticsQueryCondition() {
        this.queryTime = Tool.getNearlyOneDaysDateSlot();      //默认查询一天
        this.newGoodsCode = "";
        this.oldGoodsCode = "";
    }

    public void setNewGoodsCode(String newGoodsCode) {
        if (!newGoodsCode.equals(this.newGoodsCode)) {
            this.newGoodsCode = newGoodsCode;
            this.isQueryConditionUpdate = true;
        }
    }

    public void setOldGoodsCode(String oldGoodsCode) {
        if (!oldGoodsCode.equals(this.oldGoodsCode)) {
            this.oldGoodsCode = oldGoodsCode;
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
        this.setOldGoodsCode("");
        this.setQueryTime(Tool.getNearlyOneDaysDateSlot());      //默认查询一天
    }

    public boolean isQueryConditionUpdate() {
        return isQueryConditionUpdate;
    }

    public void resetQueryConditionUpdateStatus() {
        this.isQueryConditionUpdate = false;
    }
}
