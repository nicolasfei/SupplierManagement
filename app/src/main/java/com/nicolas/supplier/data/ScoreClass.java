package com.nicolas.supplier.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "id": "2006191",
 * "name": "不按时交货",
 * "remark": "1",
 * "sort": 1,
 * "valid": "启用"
 */
public class ScoreClass {
    public String id;
    public String name;
    public String remark;
    public int sort;
    public String valid;

    public ScoreClass(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.name = object.getString("name");
            this.remark = object.getString("remark");
            this.sort = object.getInt("sort");
            this.valid = object.getString("valid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
