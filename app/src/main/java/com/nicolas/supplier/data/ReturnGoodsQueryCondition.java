package com.nicolas.supplier.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReturnGoodsQueryCondition {
    public String goodsClassId;
    public String oldGoodsId;
    public String fId;
    public String goodsId;
    public String checkTime;        //默认查询近三天的

    public ReturnGoodsQueryCondition() {
        this.goodsClassId = "";
        this.oldGoodsId = "";
        this.fId = "";
        this.goodsId = "";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 3);
        String beforeData = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.getTime());
        String nowData = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        this.checkTime = beforeData + "~" + nowData;
    }

    public void clear() {
        this.goodsClassId = "";
        this.oldGoodsId = "";
        this.fId = "";
        this.goodsId = "";
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 3);
        String beforeData = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.getTime());
        String nowData = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        this.checkTime = beforeData + "~" + nowData;
    }
}
