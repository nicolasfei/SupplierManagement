package com.nicolas.supplier.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderQueryCondition {
    private String branchID;               //分店
    private List<String> storeRoomIDs;      //库房
    private String oldGoodsId;           //旧货号
    private String goodsId;              //货号
    private List<String> goodsClassIds;   //货物类型
    private OrderClass orderType;       //下单类型
    private PrintStatus isPrint;        //是否打印
    private OrderStatus inState;         //发货状态
    private String createTime;           //下单时间
    private String roomReceiveTime;      //库房发货时间

    private boolean isQueryConditionUpdate = false; //查询条件是否更新

    public OrderQueryCondition() {
        this.branchID = "";
        this.storeRoomIDs = new ArrayList<>();
        this.oldGoodsId = "";
        this.goodsId = "";
        this.goodsClassIds = new ArrayList<>();
        this.orderType = new OrderClass(OrderClass.NONE);
        this.isPrint = new PrintStatus(PrintStatus.NONE);
        this.inState = new OrderStatus(OrderStatus.NONE);
        this.createTime = "";
        this.roomReceiveTime = "";
    }

    public void clear() {
        setBranchID("");
        addStoreRoomID("");
        setOldGoodsId("");
        setGoodsId("");
        setOrderType(OrderClass.NONE);
        addGoodsClassId("");
        setIsPrint(PrintStatus.NONE);
        setInState(OrderStatus.NONE);
        setCreateTime("");
        setRoomReceiveTime("");
    }

    public void setBranchID(String branchID) {
        if (!this.branchID.equals(branchID)) {
            this.branchID = branchID;
            this.isQueryConditionUpdate = true;
        }
    }

    public String getBranchID() {
        return branchID;
    }

    public void addStoreRoomID(String storeRoomID) {
        boolean isHave = false;
        if (TextUtils.isEmpty(storeRoomID)) {
            if (this.storeRoomIDs.size() > 0) {
                this.storeRoomIDs.clear();
                this.isQueryConditionUpdate = true;
            }
            return;
        }

        for (String room : this.storeRoomIDs) {
            if (room.equals(storeRoomID)) {
                isHave = true;
                break;
            }
        }
        if (!isHave) {
            //这里只能查询一个，不能查询多个
            this.storeRoomIDs.clear();
            this.storeRoomIDs.add(storeRoomID);
            this.isQueryConditionUpdate = true;
        }
    }

    public String getStoreRoomID() {
        return storeRoomIDs.size() > 0 ? storeRoomIDs.get(0) : null;
    }

    public void setOldGoodsId(String oldGoodsId) {
        if (!this.oldGoodsId.equals(oldGoodsId)) {
            this.oldGoodsId = oldGoodsId;
            this.isQueryConditionUpdate = true;
        }
    }

    public String getOldGoodsId() {
        return oldGoodsId;
    }

    public void setGoodsId(String goodsId) {
        if (!this.goodsId.equals(goodsId)) {
            this.goodsId = goodsId;
            this.isQueryConditionUpdate = true;
        }
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void addGoodsClassId(String goodsClassId) {
        boolean isHave = false;
        if (TextUtils.isEmpty(goodsClassId)) {
            if (this.goodsClassIds.size() > 0) {
                this.goodsClassIds.clear();
                this.isQueryConditionUpdate = true;
            }
            return;
        }
        for (String room : this.goodsClassIds) {
            if (room.equals(goodsClassId)) {
                isHave = true;
                break;
            }
        }
        if (!isHave) {
            //这里只能查询一个，不能查询多个
            this.goodsClassIds.clear();
            this.goodsClassIds.add(goodsClassId);
            this.isQueryConditionUpdate = true;
        }
    }

    public String getGoodsClassId() {
        return goodsClassIds.size() > 0 ? goodsClassIds.get(0) : null;
    }

    public void setOrderType(String orderType) {
        if (!this.orderType.getType().equals(orderType)) {
            this.orderType.updateStatus(orderType);
            this.isQueryConditionUpdate = true;
        }
    }

    public String getOrderType() {
        return orderType.getType();
    }

    public void setIsPrint(String isPrint) {
        if (!this.isPrint.getStatus().equals(isPrint)) {
            this.isPrint.updateStatus(isPrint);
            this.isQueryConditionUpdate = true;
        }
    }

    public String getIsPrint() {
        return isPrint.getStatus();
    }

    public void setInState(String state) {
        if (!this.inState.getShowContent().equals(state)) {
            this.inState.valueOf(state);
            this.isQueryConditionUpdate = true;
        }
    }

    public String getInState() {
        return inState.getStatus();
    }

    public void setCreateTime(String createTime) {
        if (!this.createTime.equals(createTime)) {
            this.createTime = createTime;
            this.isQueryConditionUpdate = true;
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setRoomReceiveTime(String roomReceiveTime) {
        if (!this.roomReceiveTime.equals(roomReceiveTime)) {
            this.roomReceiveTime = roomReceiveTime;
            this.isQueryConditionUpdate = true;
        }
    }

    public String getRoomReceiveTime() {
        return roomReceiveTime;
    }

    public boolean isQueryConditionUpdate() {
        return isQueryConditionUpdate;
    }

    public void resetQueryConditionUpdateStatus() {
        this.isQueryConditionUpdate = false;
    }
}
