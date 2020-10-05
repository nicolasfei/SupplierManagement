package com.nicolas.supplier.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 订单商品统计类
 */
public class OrderGoodsStatistics {
    public String goodsClassId = "";     //"17",
    public String goodsClassName = "";   //"外套",
    public String color = "";            //"烟灰色L",
    public String size = "";             //"均码",
    public String goodsId = "";          //"00818BF",
    public String oldGoodsId = "";       //"11005",
    public int totalAll = 0;             //7

    public OrderGoodsStatistics(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.goodsClassId = object.getString("goodsClassId");
            this.goodsClassName = object.getString("goodsClassName");
            this.color = object.getString("color");
            this.size = object.getString("size");
            this.goodsId = object.getString("goodsId");
            this.oldGoodsId = object.getString("oldGoodsId");
            this.totalAll = object.getInt("totalAll");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
