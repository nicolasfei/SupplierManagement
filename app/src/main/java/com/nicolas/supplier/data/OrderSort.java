package com.nicolas.supplier.data;

public enum OrderSort {
    
    SORT_OrderTime(OrderInformation.sort_order_time),
    SORT_Branch(OrderInformation.sort_branch),
    SORT_Warehouse(OrderInformation.sort_warehouse),
    SORT_OrderClass(OrderInformation.sort_order_class),
    SORT_OrderNumRise(OrderInformation.sort_order_num_rise),
    SORT_OrderNumDrop(OrderInformation.sort_order_num_drop),

    SORT_OrderSwited(OrderGoodsIDClass.sort_order_code_swinted),
    SORT_OrderCodeID(OrderGoodsIDClass.sort_order_code_id);
    
    private String rule;
    OrderSort(String rule){
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }
}
