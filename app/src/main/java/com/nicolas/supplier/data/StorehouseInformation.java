package com.nicolas.supplier.data;

import com.nicolas.componentlibrary.multileveltree.TreeNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * "id": "B1201",
 * "pId": "men",
 * "code": "B1201",
 * "name": "金牛之心12楼1号",
 * "remark": "金牛之心12楼1号",
 * "sort": 100,
 * "valid": "启用"
 */
public class StorehouseInformation extends TreeNode implements Serializable {
    public String code;     //"B1201",
    public String remark;   //"金牛之心12楼1号",

    public StorehouseInformation(String json){
        super(json);
    }

    @Override
    public void setAttributeValue(String json) {
        super.setAttributeValue(json);
        try {
            JSONObject object = new JSONObject(json);
            this.code = object.getString("code");
            this.remark = object.getString("remark");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
