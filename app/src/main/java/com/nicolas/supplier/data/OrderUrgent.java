package com.nicolas.supplier.data;

public class OrderUrgent {

    public static final String URGENT_COMMON = "普通";
    public static final String URGENT_URGENT = "加急";

    public static String[] getValues() {
        return new String[]{URGENT_COMMON, URGENT_URGENT};
    }
}
