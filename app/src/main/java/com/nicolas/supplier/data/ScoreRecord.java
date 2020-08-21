package com.nicolas.supplier.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "1111",
 * "supplierId": "9527",
 * "supplierName": "9527 丫头弟弟",
 * "scoreClassId": "2006191",
 * "scoreClassName": "不按时交货",
 * "goodsId": "200423DF",
 * "val": "20.00",
 * "balance": "300.00",
 * "remark": "违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作违规操作",
 * "recordTime": "2020-06-21 14:12:53",
 * "opTime": "2020-06-21 14:12:55",
 * "valid": "启用"
 */
public class ScoreRecord {
    public String id;
    public String supplierId;
    public String supplierName;
    public String scoreClassId;
    public String scoreClassName;
    public String goodsId;
    public String val;
    public String balance;
    public String remark;
    public String recordTime;
    public String opTime;
    public String valid;

    public ScoreRecord(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.supplierId = object.getString("supplierId");
            this.supplierName = object.getString("supplierName");
            this.scoreClassId = object.getString("scoreClassId");
            this.scoreClassName = object.getString("scoreClassName");
            this.goodsId = object.getString("goodsId");
            this.val = object.getString("val");
            this.balance = object.getString("balance");
            this.remark = object.getString("remark");
            this.recordTime = object.getString("recordTime");
            this.opTime = object.getString("opTime");
            this.valid = object.getString("valid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
