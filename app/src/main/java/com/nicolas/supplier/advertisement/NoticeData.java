package com.nicolas.supplier.advertisement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoticeData extends BaseData {
    private String data;
    private final static long validTime = (30 * 24 * 60 * 60 * 1000);  //数据有效期设置为1个月

    public NoticeData(String data, long startTime) {
        super(startTime);
        this.data = data;
    }

    public String getData() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(super.dataStartTime)) + ":" + data;
    }

    @Override
    public boolean checkDataValid() {
        return (System.currentTimeMillis() - super.dataStartTime) > validTime;
    }
}
