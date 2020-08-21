package com.nicolas.supplier.supplier;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "2006165",
 * "sId": "9527",
 * "name": "丫头弟弟",
 * "supplierClass": "女装",
 * "address": "金牛之心",
 * "contactName": "沈勇",
 * "contactTel": "189****503",
 * "c_b_Bank_Id": null,
 * "bank_Id": "ICBC",
 * "bank_Name": "工商银行",
 * "bank_Logo": "#icon-zhongguogongshangyinhang1",
 * "bankPerson": "XX女装公司",
 * "bankId": "6212****7188",
 * "area": "2",
 * "creditNum": 0,
 * "createTime": null,
 * "remark": "",
 * "sort": 100,
 * "valid": "启用"
 */
public class Supplier {
    public String id;           //编号
    public String sid;          //sid编号
    public String name;         //名称
    public String supplierClass;    //货物类型
    public String contactName;      //联系人--姓名
    public String contactTel;       //联系人--电话号码
    public String area;         //地区
    public String address;      //地址
    public int creditNum;       //信誉分
    public String remark;       //备注
    public String c_b_Bank_Id;
    public String bank_Id;
    public String bank_Name;
    public String bank_Logo;
    public String bankPerson;
    public String bankId;

    public String createTime;
    public String sort;
    public String valid;

    public String userName;     //用户名
    public String passWord;     //密码

    public void setSupplierInformation(String json) throws JSONException {
        Log.d("", "setSupplierInformation: "+json);
        JSONObject object = new JSONObject(json);
        this.id = object.getString("id");
        this.sid = object.getString("sId");
        this.name = object.getString("name");
        this.supplierClass = object.getString("supplierClass");

        this.contactName = object.getString("contactName");
        this.contactTel = object.getString("contactTel");
        this.area = object.getString("area");
        this.address = object.getString("address");
        this.creditNum = Integer.parseInt(object.getString("creditNum"));

        this.remark = object.getString("remark");
        this.c_b_Bank_Id = object.getString("c_b_Bank_Id");
        this.bank_Id = object.getString("bank_Id");
        this.bank_Name = object.getString("bank_Name");
        this.bank_Logo = object.getString("bank_Logo");

        this.bankPerson = object.getString("bankPerson");
        this.bankId = object.getString("bankId");
        this.createTime = object.getString("createTime");
        this.sort = object.getString("sort");
        this.valid = object.getString("valid");
    }
}
