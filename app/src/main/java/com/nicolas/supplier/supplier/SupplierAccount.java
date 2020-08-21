package com.nicolas.supplier.supplier;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "2006181",
 * "supplierId": "2006165",
 * "supplierName": "9527 丫头弟弟",
 * "userName": "killing001",
 * "tel": "18224407848",
 * "password": null,
 * "name": "沈勇",
 * "sex": "男",
 * "createTime": null,
 * "remark": "",
 * "sort": 100,
 * "valid": "启用"
 */
public class SupplierAccount {
    public String id;
    public String supplierId;
    public String supplierName;
    public String userName;
    public String tel;
    public String password;
    public String name;
    public String sex;
    public String createTime;
    public String remark;
    public String sort;
    public String valid;

    public SupplierAccount() {
        this.id = "";
        this.supplierId = "";
        this.supplierName = "";
        this.userName = "";
        this.tel = "";
        this.password = "";
        this.name = "";
        this.sex = "";
        this.createTime = "";
        this.remark = "";
        this.sort = "";
        this.valid = "";
    }

    public SupplierAccount(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.id = jsonObject.getString("id");
        this.supplierId = jsonObject.getString("supplierId");
        this.supplierName = jsonObject.getString("supplierName");
        this.userName = jsonObject.getString("userName");
        this.tel = jsonObject.getString("tel");
        this.password = jsonObject.getString("password");
        this.name = jsonObject.getString("name");
        this.sex = jsonObject.getString("sex");
        this.createTime = jsonObject.getString("createTime");
        this.remark = jsonObject.getString("remark");
        this.sort = jsonObject.getString("sort");
        this.valid = jsonObject.getString("valid");
    }

    public void setSupplierAccountInformation(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.id = jsonObject.getString("id");
        this.supplierId = jsonObject.getString("supplierId");
        this.supplierName = jsonObject.getString("supplierName");
        this.userName = jsonObject.getString("userName");
        this.tel = jsonObject.getString("tel");
        this.password = jsonObject.getString("password");
        this.name = jsonObject.getString("name");
        this.sex = jsonObject.getString("sex");
        this.createTime = jsonObject.getString("createTime");
        this.remark = jsonObject.getString("remark");
        this.sort = jsonObject.getString("sort");
        this.valid = jsonObject.getString("valid");
    }
}
