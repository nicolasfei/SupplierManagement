package com.nicolas.supplier.ui.my.user;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.ScoreClass;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.score.ScoreInterface;
import com.nicolas.supplier.supplier.Supplier;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView about = findViewById(R.id.about);

        //获取app当前版本
        String appCurrentVersion = "";
        PackageManager manager = SupplierApp.getInstance().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(SupplierApp.getInstance().getPackageName(), 0);
            appCurrentVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String value = "App名称：" +
                getString(R.string.app_name) + " \n" +
                "版本号：" +
                appCurrentVersion + " \n" +
                "\n" +
                "系统名称：\n" +
                "DAWN BUSINESS IT SYSTEM \n" +
                "\n" +
                "版权信息：\n" +
                "Copyright  2009-2020 By Si Chuan Province Dawn Business CO.,Ltd.All rights Reserved.";
        about.setText(value);

//        Button test = findViewById(R.id.testButton);
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                testSend();
//            }
//        });
    }

    private void testSend() {
//        CommandResponse.setTest=true;
//        query();
        CommandResponse c = null;
        c.reLogin = false;
    }

    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_SCORE_RECORD;
        vo.url = ScoreInterface.ScoreClassQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(1));
        parameters.put("pageSize", String.valueOf(64));
        parameters.put("pageCount", String.valueOf(0));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ScoreInterface.ScoreClassQuery:
//                    if (result.success) {
//                        try {
//                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
//                                scoreClasses.clear();
//                            }
//                            JSONArray array = new JSONArray(result.data);
//                            for (int i = 0; i < array.length(); i++) {
//                                ScoreClass item = new ScoreClass(array.getString(i));
//                                scoreClasses.add(item);
//                            }
//                            pageCount = result.total;
//                            scoreClassQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            scoreClassQueryResult.setValue(new OperateResult(new OperateError(-1,
//                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
//                        }
//                    } else {
//                        scoreClassQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
//                    }
                    break;
                default:
                    break;
            }
        }
    };
}
