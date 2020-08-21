package com.nicolas.supplier.advertisement;

public abstract class BaseData {
    public long dataStartTime;  //数据到达时间
    public boolean isValid;     //是否有效

    public BaseData(long dataStartTime) {
        this.dataStartTime = dataStartTime;
        this.isValid = checkDataValid();
    }

    /**
     * 检查数据是否有效
     *
     * @return 数据是否有效
     */
    public abstract boolean checkDataValid();
}
