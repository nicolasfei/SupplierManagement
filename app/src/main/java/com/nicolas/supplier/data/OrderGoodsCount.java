package com.nicolas.supplier.data;

import java.util.HashMap;
import java.util.Map;

public class OrderGoodsCount {
    public String goodsID;
    public String oldGoodsID;
    public Map<String, Integer> colorSizeNum;

    public OrderGoodsCount() {
        this.colorSizeNum = new HashMap<>();
    }
}
