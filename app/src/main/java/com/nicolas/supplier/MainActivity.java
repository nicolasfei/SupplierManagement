package com.nicolas.supplier;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.app.LoginManager;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.login.LoginInterface;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppActivityManager.getInstance().addActivity(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dev, R.id.navigation_my)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //由于长时间被至于后台，系统回收了activity
        if (SupplierKeeper.getInstance().getOnDutySupplier().sid==null || TextUtils.isEmpty(SupplierKeeper.getInstance().getOnDutySupplier().sid)){
            LoginManager.getInstance().loginExpire(getString(R.string.loginTimeOut));
            finish();
            return;
        }

        //--------------初始化全局类-------------------//
        //开启打印机连接任务
        PrinterManager.getInstance().resetLinkDeviceModel(SupplierKeeper.getInstance().getOnDutySupplier().sid);
        PrinterManager.getInstance().init(SupplierApp.getInstance());
        //开启SupplierKeeper定时查询任务
        SupplierKeeper.getInstance().startTimerTask();
        //初始化url图片缓存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        ImageLoadClass.getInstance().init(BitmapFactory.decodeResource(getResources(), R.mipmap.ico_big_decolor, options));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(R.string.logout)
                .setMessage(R.string.user_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLogout();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * 用户登出
     */
    private void userLogout() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_LOGIN;
        vo.url = LoginInterface.Logout;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case LoginInterface.Logout:        //登陆
                    if (!result.success) {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_failed) + "," + result.msg);
                    } else {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_success));
                    }
                    MainActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy: ");
        //关闭定时查询任务
        SupplierKeeper.getInstance().cancelTimerTask();
        //打印机模块注销
        PrinterManager.getInstance().unManager();
        //释放
        ImageLoadClass.getInstance().release();
        super.onDestroy();
        AppActivityManager.getInstance().removeActivity(this);
        Log.d("MainActivity", "onDestroy: finish！");
    }
}
