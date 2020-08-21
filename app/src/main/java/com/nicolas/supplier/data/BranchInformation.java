package com.nicolas.supplier.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "106",
 * "fId": "M09",
 * "newName": null,
 * "name": null,
 * "password": null,
 * "xsName": null,
 * "goodsClassId": "3",
 * "goodsClassName": "女装",
 * "dearClassId": null,
 * "dearClassName": null,
 * "dzName": null,
 * "dzTel": null,
 * "province": "四川省",
 * "city": "成都市",
 * "district": "邛崃市",
 * "town": "文君街道",
 * "address": null,
 * "storeRoomId": "A1101",
 * "storeRoomName": "金牛之心11楼1号",
 * "areaId": null,
 * "areaName": null,
 * "areaTeId": null,
 * "areaTeName": null,
 * "complaintTel": null,
 * "branchType": null,
 * "purchaseTypeJson": null,
 * "refuseId": null,
 * "refuse": null,
 * "priceLine": 0,
 * "autoOrder": null,
 * "multiple": 0,
 * "balance": 0,
 * "freezeMoney": 0,
 * "availableMoney": 0,
 * "weChat": null,
 * "weMall": null,
 * "state": null,
 * "remark": null,
 * "sort": 0,
 * "valid": null
 */
public class BranchInformation {
    public String id;           //"106",
    public String fId;          //"M09",
    public String newName;      //null,
    public String name;         //null,
    public String password;     //null,
    public String xsName;       //null,
    //public String goodsClassId; //"3",
    //public String goodsClassName;//"女装",
    public String branchClass;  //店铺类型
    public String dearClassId;  //null,
    public String dearClassName;//null,
    public String dzName;       //null,
    public String dzTel;        //null,
    public String province;     //"四川省",
    public String city;         //"成都市",
    public String district;     //"邛崃市",
    //    public String town;         //"文君街道",
    public String address;      //null,
    public String storeRoomId;  //"A1101",
    public String storeRoomName;//"金牛之心11楼1号",
    public String areaId;       //null,
    public String areaName;     //null,
    public String areaTeId;     //null,
    public String areaTeName;   //null,
    public String complaintTel; //null,
    public String branchType;   //null,
    public String purchaseTypeJson;//null,
    public String refuseId;     //null,
    public String refuse;       //null,
    public float priceLine;     //0,
    public String autoOrder;    //null,
    public int multiple;        //0,
    public float balance;       //0,
    public float freezeMoney;   //0,
    public String availableMoney;//0,
    public String weChat;       //null,
    public String weMall;       //null,
    public String state;        //null,
    public String remark;       //null,
    public int sort;           //0,
    public String valid;        //null

    public BranchInformation() {

    }

    public BranchInformation(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.fId = object.getString("fId");
            this.newName = object.getString("newName");
            this.name = object.getString("name");
            this.password = object.getString("password");
            this.xsName = object.getString("xsName");
            //this.goodsClassId = object.getString("goodsClassId");
            //this.goodsClassName = object.getString("goodsClassName");
            this.branchClass = object.getString("branchClass");
            this.dearClassId = object.getString("dearClassId");
            this.dearClassName = object.getString("dearClassName");
            this.dzName = object.getString("dzName");
            this.dzTel = object.getString("dzTel");
            this.province = object.getString("province");
            this.city = object.getString("city");
            this.district = object.getString("district");
//        this.town = object.getString("town");
            this.address = object.getString("address");
            this.storeRoomId = object.getString("storeRoomId");
            this.storeRoomName = object.getString("storeRoomName");
            this.areaId = object.getString("areaId");
            this.areaName = object.getString("areaName");
            this.areaTeId = object.getString("areaTeId");
            this.areaTeName = object.getString("areaTeName");
            this.complaintTel = object.getString("complaintTel");
            this.branchType = object.getString("branchType");
            this.purchaseTypeJson = object.getString("purchaseTypeJson");
            this.refuseId = object.getString("refuseId");
            this.refuse = object.getString("refuse");
            this.priceLine = Float.parseFloat(object.getString("priceLine"));
            this.autoOrder = object.getString("autoOrder");
            this.multiple = Integer.parseInt(object.getString("multiple"));
            this.balance = Float.parseFloat(object.getString("balance"));
            this.freezeMoney = Float.parseFloat(object.getString("freezeMoney"));
            this.availableMoney = object.getString("availableMoney");
            this.weChat = object.getString("weChat");
            this.weMall = object.getString("weMall");
            this.state = object.getString("state");
            this.remark = object.getString("remark");
            this.sort = Integer.parseInt(object.getString("sort"));
            this.valid = object.getString("valid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
