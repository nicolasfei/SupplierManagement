package com.nicolas.supplier.server;

import android.util.Log;

import com.nicolas.toollibrary.HttpHandler;

public abstract class AbstractInterface {

    private static final String TAG = "AbstractInterface";
    public static final String COMMAND_URL = "https://supplier.scdawn.com/";

    private AbstractInterface nextHandler;

    public final String handleMessage(CommandVo vo) {
        String result = null;
        //判断是否自己处理的接口
        if (vo.url.equals(this.getUrlParam())) {
            result = this.echo(vo);
        } else {
            if (this.nextHandler != null) {
                result = this.nextHandler.handleMessage(vo);
            } else {
                Log.d(TAG, "handleMessage: can not handle this command:" + vo.url);
            }
        }
        return result;
    }

    //设置下一个处理者
    public void setNextHandler(AbstractInterface nextHandler) {
        this.nextHandler = nextHandler;
    }

    //获取处理者处理的接口
    public abstract String getUrlParam();

    //处理者的处理任务
    public String echo(CommandVo vo){
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode,vo.token, vo.contentType);
    }
}
