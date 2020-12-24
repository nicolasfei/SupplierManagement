package com.nicolas.supplier.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.nicolas.supplier.R;
import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.GlobalCrashHandler;
import com.nicolas.supplier.FirstActivity;

public class SupplierApp extends Application {

    private static SupplierApp app;
    private String apkSavePath;
    private String TAG = "SupplierApp";

    public static SupplierApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        this.apkSavePath = getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "prxd.apk";
        //初始化异常处理
        GlobalCrashHandler.getInstance().init(app, "",
                getString(R.string.emailForm), getString(R.string.formPassword),
                getString(R.string.emailTo), getString(R.string.emailContentHead), FirstActivity.class);
    }

    public String getApkSavePath() {
        return apkSavePath;
    }


    /**
     * app再后台被系统回收时，清理掉所有activity
     *
     * @param level TRIM_MEMORY_COMPLETE：   内存不足，并且该进程在后台进程列表最后一个，马上就要被清理
     *              TRIM_MEMORY_MODERATE：   内存不足，并且该进程在后台进程列表的中部。
     *              TRIM_MEMORY_BACKGROUND： 内存不足，并且该进程是后台进程。
     *              TRIM_MEMORY_UI_HIDDEN：  内存不足，并且该进程的UI已经不可见了
     *              TRIM_MEMORY_COMPLETE：   这个监听的时候有时候监听不到，建议监听TRIM_MEMORY_MODERATE，在这个里面处理退出程序操作。
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "onTrimMemory: level is " + level);
        if (level == TRIM_MEMORY_MODERATE) {
            LoginManager.getInstance().loginExpire(getString(R.string.loginAgain));
        }
    }
}
