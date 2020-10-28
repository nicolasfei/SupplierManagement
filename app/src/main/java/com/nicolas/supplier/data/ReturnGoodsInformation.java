package com.nicolas.supplier.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 返货信息
 */
public class ReturnGoodsInformation {
    public String id;               //"2008212",
    public String sId;              //"A002",
    public String supplierId;       //"002",
    public String supplierName;     //"2017丫头",
    public String goodsClassId;     //"13",
    public String goodsClassName;   //"T恤",
    public String oldGoodsId;       //"6086",
    public String goodsId;          //"705166B",
    public String purchaseType;     //"自选单",
    public String b_b_Stock_Id;     //null,
    public String b_g_GoodsId_Id;   //null,
    public String branchName;       //null,
    public String fId;              //"ZA0062",
    public String branchId;         //null,
    public String b_c_DearClass_Id; //null,
    public String img;              //"https://file.scdawn.com/cloud/goodsImg/A002/T恤/705166B/170516083204602F9.jpg",
    public int backNumber;          //1,
    public float backPrice;         //25,
    public float backTotalPrice;    //25,
    public String backTime;         //"",
    public String state;            //"已审核",
    public String checkTime;        //"2020-08-21 11:28:59",
    public String b_b_BarCode_Id;   //"170529FC808D",
    public String s_b_Account_Id;   //null,
    public String code;             //"200821112858794",
    public String remark;           //"库房返货",
    public String valid;            //"启用"

    public ReturnGoodsInformation(String json){
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")){
                this.id = object.getString("id");
            }
            if (object.has("sId")){
                this.sId = object.getString("sId");
            }
            if (object.has("supplierId")){
                this.supplierId = object.getString("supplierId");
            }
            if (object.has("supplierName")){
                this.supplierName = object.getString("supplierName");
            }
            if (object.has("goodsClassId")){
                this.goodsClassId = object.getString("goodsClassId");
            }
            if (object.has("goodsClassName")){
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("oldGoodsId")){
                this.oldGoodsId = object.getString("oldGoodsId");
            }
            if (object.has("goodsId")){
                this.goodsId = object.getString("goodsId");
            }

            if (object.has("purchaseType")){
                this.purchaseType = object.getString("purchaseType");
            }
            if (object.has("b_b_Stock_Id")){
                this.b_b_Stock_Id = object.getString("b_b_Stock_Id");
            }
            if (object.has("b_g_GoodsId_Id")){
                this.b_g_GoodsId_Id = object.getString("b_g_GoodsId_Id");
            }
            if (object.has("branchName")){
                this.branchName = object.getString("branchName");
            }
            if (object.has("fId")){
                this.fId = object.getString("fId");
            }
            if (object.has("branchId")){
                this.branchId = object.getString("branchId");
            }
            if (object.has("b_c_DearClass_Id")){
                this.b_c_DearClass_Id = object.getString("b_c_DearClass_Id");
            }
            if (object.has("img")){
                this.img = object.getString("img");
            }
            if (object.has("backNumber")){
                this.backNumber = object.getInt("backNumber");
            }

            if (object.has("backPrice")){
                this.backPrice = (float) object.getDouble("backPrice");
            }
            if (object.has("backTotalPrice")){
                this.backTotalPrice = (float) object.getDouble("backTotalPrice");
            }
            if (object.has("backTime")){
                this.backTime = object.getString("backTime");
            }
            if (object.has("state")){
                this.state = object.getString("state");
            }
            if (object.has("checkTime")){
                this.checkTime = object.getString("checkTime");
            }
            if (object.has("b_b_BarCode_Id")){
                this.b_b_BarCode_Id = object.getString("b_b_BarCode_Id");
            }
            if (object.has("s_b_Account_Id")){
                this.s_b_Account_Id = object.getString("s_b_Account_Id");
            }
            if (object.has("code")){
                this.code = object.getString("code");
            }
            if (object.has("remark")){
                this.remark = object.getString("remark");
            }
            if (object.has("valid")){
                this.valid = object.getString("valid");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
