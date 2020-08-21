package com.nicolas.supplier.data;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 货号类
 * <p>
 * {
 * "id;//"2007101",
 * "supplierId;//"9527",
 * "supplierName;//null,
 * "goodsClassId;//"2",
 * "goodsClassName;//"男装",
 * "seasonName;//"夏装",
 * "customer;//"少女款",
 * "oldGoodsId;//"1213",
 * "goodsId;//"200710201",
 * "priceRangeId;//null,
 * "img;//"http://localhost:50386/cloud/images/goods/20200710/20071091094e89-a429-45e2-9449-bc7f158bd5f0-5.jpg",
 * "inPrice;//"30.00",
 * "originalPrice;//"30.00",
 * "orderPrice;//"30.00",
 * "salePrice;//null,
 * "recordCode;//null,
 * "recordData;//null,
 * "deliveryDate;//1,
 * "purchaseType;//null,
 * "purchaseTypeId;//null,
 * "purchaseTypeName;//null,
 * "goodsUnit;//"件",
 * "goodsType;//"普通",
 * "isReplenishment;//null,
 * "isStock;//"允许",
 * "isUpMall;//null,
 * "isBack;//null,
 * "createTime;//"2020-07-10 09:41",
 * "seeList;//null,
 * "supplyList;//null,
 * "areaList;//null,
 * "accountId;//null,
 * "accountName;//null,
 * "accountIdCheck;//null,
 * "accountNameCheck;//null,
 * "remark;//"",
 * "sort;//100,
 * "valid;//"启用",
 * "property;//[
 * {
 * "id;//"2007111",
 * "gId;//"2007111",
 * "color;//"白色",
 * "size;//"M",
 * "isStock;//"允许",
 * "sort;//"1",
 * "valid;//"启用"
 * },
 * {
 * "id;//"2007112",
 * "gId;//"2007112",
 * "color;//"白色",
 * "size;//"XL",
 * "isStock;//"允许",
 * "sort;//"2",
 * "valid;//"启用"
 * },
 * {
 * "id;//"2007113",
 * "gId;//"2007113",
 * "color;//"白色",
 * "size;//"XXL",
 * "isStock;//"允许",
 * "sort;//"3",
 * "valid;//"启用"
 * },
 * {
 * "id;//"2007114",
 * "gId;//"2007114",
 * "color;//"红色",
 * "size;//"M",
 * "isStock;//"允许",
 * "sort;//"4",
 * "valid;//"启用"
 * },
 * {
 * "id;//"2007115",
 * "gId;//"2007115",
 * "color;//"红色",
 * "size;//"XL",
 * "isStock;//"允许",
 * "sort;//"5",
 * "valid;//"启用"
 * },
 * {
 * "id;//"2007116",
 * "gId;//"2007116",
 * "color;//"红色",
 * "size;//"XXL",
 * "isStock;//"允许",
 * "sort;//"6",
 * "valid;//"启用"
 * }
 * ]
 * }
 */
public class GoodsCode {
    //    public Drawable photo;
    public String id;                   //"2007101",
    public String supplierId;           //"9527",
    public String supplierName;         //null,
    public String goodsClassId;         //"2",
    public String goodsClassName;       //"男装",
    public String seasonName;           //"夏装",
    public String customer;             //"少女款",
    public String oldGoodsId;           //"1213",
    public String goodsId;              //"200710201",
    public String priceRangeId;         //null,
    public String img;                  //"http://localhost:50386/cloud/images/goods/20200710/20071091094e89-a429-45e2-9449-bc7f158bd5f0-5.jpg",
    public String inPrice;              //"30.00",
    public String originalPrice;        //"30.00",
    public String orderPrice;           //"30.00",
    public String salePrice;            //null,
    public String recordCode;           //null,
    public String recordData;           //null,
    public int deliveryDate;            //1,
    public String purchaseType;         //null,
    public String purchaseTypeId;       //null,
    public String purchaseTypeName;     //null,
    public String goodsUnit;            //"件",
    public String goodsType;            //"普通",
    public String isReplenishment;      //null,
    public String isStock;              //"允许",
    public String isUpMall;             //null,
    public String isBack;               //null,
    public String createTime;           //"2020-07-10 09:41",
    public String seeList;              //null,
    public String supplyList;           //null,
    public String areaList;             //null,
    public String accountId;            //null,
    public String accountName;          //null,
    public String accountIdCheck;       //null,
    public String accountNameCheck;     //null,
    public String remark;               //"",
    public int sort;                    //100,
    public String valid;                //"启用",
    public List<Property> properties;

    public GoodsCode(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.supplierId = object.getString("supplierId");
            this.supplierName = object.getString("supplierName");
            this.goodsClassId = object.getString("goodsClassId");
            this.goodsClassName = object.getString("goodsClassName");
            this.seasonName = object.getString("seasonName");
            this.customer = object.getString("customer");

            this.oldGoodsId = object.getString("oldGoodsId");
            this.goodsId = object.getString("goodsId");
            this.priceRangeId = object.getString("priceRangeId");
            this.img = object.getString("img");
            this.inPrice = object.getString("inPrice");
            this.originalPrice = object.getString("originalPrice");

            this.orderPrice = object.getString("orderPrice");
            this.salePrice = object.getString("salePrice");
            this.recordCode = object.getString("recordCode");
            this.recordData = object.getString("recordData");
            this.deliveryDate = object.getInt("deliveryDate");
            this.purchaseType = object.getString("purchaseType");

            this.purchaseTypeId = object.getString("purchaseTypeId");
            this.purchaseTypeName = object.getString("purchaseTypeName");
            this.goodsUnit = object.getString("goodsUnit");
            this.goodsType = object.getString("goodsType");
            this.isReplenishment = object.getString("isReplenishment");
            this.isStock = object.getString("isStock");

            this.isUpMall = object.getString("isUpMall");
            this.isBack = object.getString("isBack");
            this.createTime = object.getString("createTime");
            this.seeList = object.getString("seeList");
            this.supplyList = object.getString("supplyList");
            this.areaList = object.getString("areaList");

            this.accountId = object.getString("accountId");
            this.accountName = object.getString("accountName");
            this.accountIdCheck = object.getString("accountIdCheck");
            this.accountNameCheck = object.getString("accountNameCheck");
            this.remark = object.getString("remark");
            this.sort = object.getInt("sort");
            this.valid = object.getString("valid");

            this.properties = new ArrayList<>();
            JSONArray array = new JSONArray(object.getJSONArray("property"));
            for (int i = 0; i < array.length(); i++) {
                this.properties.add(new Property(array.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * {
     * * "id;//"2007116",
     * * "gId;//"2007116",
     * * "color;//"红色",
     * * "size;//"XXL",
     * * "isStock;//"允许",
     * * "sort;//"6",
     * * "valid;//"启用"
     * * }
     */
    public class Property {
        public String id;
        public String gId;
        public String color;
        public String size;
        public String isStock;
        public String sort;
        public String valid;

        public Property(String json) {
            try {
                JSONObject object = new JSONObject(json);
                this.id = object.getString("id");
                this.gId = object.getString("gId");
                this.color = object.getString("color");
                this.size = object.getString("size");
                this.isStock = object.getString("isStock");
                this.sort = object.getString("sort");
                this.valid = object.getString("valid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
