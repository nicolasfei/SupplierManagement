package com.nicolas.supplier.server;

import android.text.TextUtils;
import com.nicolas.toollibrary.Tool;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandResponse {
    public boolean success = false;
    public String msg = "";
    public int code = 0;
    public String data;
    public String jsonData;
    public String sta;          //统计数据
    public int total;
    public CommandTypeEnum typeEnum;
    public String url;
    public boolean reLogin = false;     //重新登陆

    public static boolean setTest = false;

    public CommandResponse(String response, String requestUrl) {
        Tool.longPrint("CommandResponse: " + response + " requestUrl is " + requestUrl);
        this.url = requestUrl;
        if (setTest){
            setTest=false;
            reLogin = true;
            return;
        }
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject rep = new JSONObject(response);
                this.success = rep.getBoolean("success");
                if (rep.has("msg")) {
                    this.msg = rep.getString("msg");
                    if (this.msg.equals("请重新登录")) {
                        reLogin = true;
                    }
                }
                if (rep.has("data")) {
                    this.data = rep.getString("data");
                }
                if (rep.has("token")) {
                    JSONObject token = new JSONObject();
                    token.put("token", rep.getString("token"));
                    this.data = token.toString();
                }
                if (rep.has("jsonData")) {
                    this.jsonData = rep.getString("jsonData");
                }
                if (rep.has("sta")) {        //统计数据
                    this.sta = rep.getString("sta");
                }
                if (rep.has("total")) {
                    this.total = rep.getInt("total");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (response.contains("error")) {
                    this.msg = response.substring("error".length());
                } else {     //这个是app版本号
                    this.success = true;
                    this.data = response;
                }
            }
        }
    }
}
