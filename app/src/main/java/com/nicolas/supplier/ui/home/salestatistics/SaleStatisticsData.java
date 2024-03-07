package com.nicolas.supplier.ui.home.salestatistics;

import org.json.JSONException;
import org.json.JSONObject;

public class SaleStatisticsData {
    public String id = "";//"20111220",
    public String goodsId = "";//"01112020",
    public String img = "";//"https://file.scdawn.com/cloud/images/goods/20201112/2011127963a7ef-dd5c-4742-9159-e43884276d53.jpg",
    public String oldGoodsId = "";//"630",
    public String goodsType = "";//"特殊需求",
    public String isStock = "";//"允许",
    public int sendAmount = 0;//64,
    public int sendBook = 0;//64,           //已发货订单
    public float sendPrice = 0;//5888,
    public int saleAmount = 0;//0,
    public float salePrice = 0;//0,
    public int backAmount = 0;//0,
    public float backPrice = 0;//0,
    public int inStockAmount = 0;//1,
    public float inStockPrice = 0;//92

    public SaleStatisticsData() {

    }

    public SaleStatisticsData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("id")) {
                this.id = jsonObject.getString("id");
            }
            if (jsonObject.has("goodsId")) {
                this.goodsId = jsonObject.getString("goodsId");
            }
            if (jsonObject.has("img")) {
                this.img = jsonObject.getString("img");
            }
            if (jsonObject.has("oldGoodsId")) {
                this.oldGoodsId = jsonObject.getString("oldGoodsId");
            }
            if (jsonObject.has("goodsType")) {
                this.goodsType = jsonObject.getString("goodsType");
            }

            if (jsonObject.has("isStock")) {
                this.isStock = jsonObject.getString("isStock");
            }
            if (jsonObject.has("sendAmount")) {
                this.sendAmount = jsonObject.getInt("sendAmount");
            }
            if (jsonObject.has("sendBook")) {
                this.sendBook = jsonObject.getInt("sendBook");
            }
            if (jsonObject.has("sendPrice")) {
                this.sendPrice = (float) jsonObject.getDouble("sendPrice");
            }
            if (jsonObject.has("saleAmount")) {
                this.saleAmount = jsonObject.getInt("saleAmount");
            }
            if (jsonObject.has("salePrice")) {
                this.salePrice = (float) jsonObject.getDouble("salePrice");
            }

            if (jsonObject.has("backAmount")) {
                this.backAmount = jsonObject.getInt("backAmount");
            }
            if (jsonObject.has("backPrice")) {
                this.backPrice = (float) jsonObject.getDouble("backPrice");
            }
            if (jsonObject.has("inStockAmount")) {
                this.inStockAmount = jsonObject.getInt("inStockAmount");
            }
            if (jsonObject.has("inStockPrice")) {
                this.inStockPrice = (float) jsonObject.getDouble("inStockPrice");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
