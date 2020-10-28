package com.nicolas.supplier;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.ui.login.LoginActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.GlobalCrashHandler;

import java.io.File;

public class VersionCheckActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView checking;
    private VersionCheckViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_activity);
        this.viewModel = new ViewModelProvider(this).get(VersionCheckViewModel.class);

        this.progressBar = findViewById(R.id.progressBar3);
        this.checking = findViewById(R.id.checking);

        //app版本检测结果
        this.viewModel.getMatchVersionResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg.what == 1) {   //新版本发布
                        showChecking(getString(R.string.newVersionDowning));
                        viewModel.downNewVersionApp("http://updatesupplier.scdawn.com/supplier.apk");
                    } else {
                        dismissChecking();
                        jumpToLoginActivity();
                    }
                }

                if (operateResult.getError() != null) {
                    dismissChecking();
                    appWillShutdown();
                }
            }
        });

        //app下载结果
        this.viewModel.getNewAppDownResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissChecking();
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    String apkPath = (String) msg.obj;
                    installApp(apkPath);
                }

                if (operateResult.getError() != null) {
                    appWillShutdown();
                }
            }
        });

        showChecking(getString(R.string.newVersionChecking));
        this.viewModel.checkAppLastVersion();
    }

    private void jumpToLoginActivity() {
        Intent intent = new Intent(VersionCheckActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * App版本检测失败，即将退出
     */
    private void appWillShutdown() {
        BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.app_will_shutdown), new BruceDialog.OnAlertDialogListener() {
            @Override
            public void onSelect(boolean confirm) {
                GlobalCrashHandler.getInstance().killApp();
            }
        });
    }

    /**
     * 安装app
     *
     * @param apkPath app保存路径
     */
    private void installApp(String apkPath) {
        File apk = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, SupplierApp.getInstance().getPackageName() + ".fileProvider", apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        }
        try {
            startActivity(intent);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示进度条
     *
     * @param msg 显示信息
     */
    private void showChecking(String msg) {
        this.progressBar.setVisibility(View.VISIBLE);
        this.checking.setVisibility(View.VISIBLE);
        this.checking.setText(msg);
    }

    /**
     * 隐藏进度条
     */
    private void dismissChecking() {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.checking.setVisibility(View.INVISIBLE);
    }
}
