package com.nicolas.supplier.server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.server.common.CommonCommand;
import com.nicolas.supplier.server.goods.GoodsCommand;
import com.nicolas.supplier.server.login.LoginCommand;
import com.nicolas.supplier.server.management.ManagementCommand;
import com.nicolas.supplier.server.order.OrderCommand;
import com.nicolas.supplier.server.score.ScoreCommand;

import java.util.ArrayList;
import java.util.List;

public class Invoker {
    private OnExecResultCallback callback;
    private static Invoker invoker = new Invoker();
    private List<Command> commandList;

    private Invoker() {
        //创建所需命令
        commandList = new ArrayList<>();
        commandList.add(new LoginCommand());
        commandList.add(new CommonCommand());
        commandList.add(new OrderCommand());
        commandList.add(new GoodsCommand());
        commandList.add(new ManagementCommand());
        commandList.add(new ScoreCommand());
    }

    public static Invoker getInstance() {
        return invoker;
    }

    public void exec(CommandVo vo) {
        //线程异步执行
        TaskThread thread = new TaskThread(vo);
        thread.start();
    }

    /**
     * 同步执行
     *
     * @param vo 命令
     */
    public String synchronousExec(CommandVo vo) {
        for (Command c : commandList) {
            if (c.getCommandType() == vo.typeEnum) {
                return c.execute(vo);
            }
        }
        return null;
    }

    //设置处理结果回调接口
    public void setOnEchoResultCallback(OnExecResultCallback callback) {
        this.callback = callback;
    }

    //处理结果返回接口
    public interface OnExecResultCallback {
        void execResult(CommandResponse result);
    }

    /**
     * handler
     */
    private Handler handler = new Handler(SupplierApp.getInstance().getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    if (callback != null) {
                        Bundle b = msg.getData();
                        callback.execResult(new CommandResponse(b.getString("result"), b.getString("url")));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 线程执行
     */
    private class TaskThread extends Thread {
        private CommandVo vo;

        private TaskThread(CommandVo vo) {
            this.vo = vo;
        }

        @Override
        public void run() {
            super.run();
            for (Command c : commandList) {
                if (c.getCommandType() == vo.typeEnum) {
                    String result = c.execute(vo);
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("result", result);
                    b.putString("url", vo.url);
                    msg.setData(b);
                    handler.sendMessage(msg);
                    break;
                }
            }
        }
    }
}
