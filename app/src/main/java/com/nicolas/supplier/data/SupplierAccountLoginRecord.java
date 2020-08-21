package com.nicolas.supplier.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "11111",
 * "supplierId": "2006165",
 * "supplierName": "9527 丫头弟弟",
 * "supplierAccountId": "2006181",
 * "supplierAccountName": "沈勇",
 * "supplierAccountTel": "182****848",
 * "ip": "127.0.0.1",
 * "loginTime": "2020-06-18 09:42:58",
 * "remark": "111",
 * "valid": "启用"
 */
public class SupplierAccountLoginRecord {
    public String id;
    public String supplierId;
    public String supplierName;
    public String supplierAccountId;
    public String supplierAccountName;
    public String supplierAccountTel;
    public String ip;
    public String loginTime;
    public String remark;
    public String valid;

    public SupplierAccountLoginRecord(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.supplierId = object.getString("supplierId");
            this.supplierName = object.getString("supplierName");
            this.supplierAccountId = object.getString("supplierAccountId");

            this.supplierAccountName = object.getString("supplierAccountName");
            this.supplierAccountTel = object.getString("supplierAccountTel");
            this.ip = object.getString("ip");

            this.loginTime = object.getString("loginTime");
            this.remark = object.getString("remark");
            this.valid = object.getString("valid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
