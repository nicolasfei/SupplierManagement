package com.nicolas.supplier.advertisement;

import android.graphics.Bitmap;

public class AdvertData extends BaseData{
    private Bitmap imageRes;

    public AdvertData(Bitmap imageRes,long startTime) {
        super(startTime);
        this.imageRes = imageRes;
    }

    public Bitmap getImageRes() {
        return imageRes;
    }

    @Override
    public boolean checkDataValid() {       //广告不过期
        return true;
    }
}
