package com.nicolas.supplier.server;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandResponse {
    private static final String TAG = "CommandResponse";
    public boolean success = false;
    public String msg = "";
    public int code = 0;
    public String data;
    public String jsonData;
    public int total;
    public CommandTypeEnum typeEnum;
    public String url;

    public CommandResponse(String response, String requestUrl) {
        if (response != null) {
            try {
                Log.i(TAG, "CommandResponse: " + response + " requestUrl is " + requestUrl);
                JSONObject rep = new JSONObject(response);
                this.success = rep.getBoolean("success");
                if (rep.has("msg")) {
                    this.msg = rep.getString("msg");
                }
                if (rep.has("data")) {
                    this.data = rep.getString("data");
                    Log.d(TAG, "CommandResponse: data is " + this.data);
                }
                if (rep.has("token")) {
                    JSONObject token = new JSONObject();
                    token.put("token", rep.getString("token"));
                    this.data = token.toString();
                }
                if (rep.has("jsonData")) {
                    this.jsonData = rep.getString("jsonData");
                }
                if (rep.has("total")) {
                    this.total = rep.getInt("total");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.msg = response;
            }
        }
        this.url = requestUrl;
    }
}
