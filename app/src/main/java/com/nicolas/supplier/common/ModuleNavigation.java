package com.nicolas.supplier.common;

public class ModuleNavigation {
    public String name;
    public int bitmapID;
    public boolean isTitle;
    public Class<?> navActivity;
    private int NavigationNum;

    public ModuleNavigation(boolean isTitle, String name, int bitmapID, Class<?> navActivity) {
        this.isTitle = isTitle;
        this.bitmapID = bitmapID;
        this.name = name;
        this.navActivity = navActivity;
    }

    public void setNavigationNum(int navigationNum) {
        NavigationNum = navigationNum;
    }

    public int getNavigationNum() {
        return NavigationNum;
    }
}

