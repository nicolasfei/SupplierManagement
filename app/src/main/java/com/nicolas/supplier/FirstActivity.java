package com.nicolas.supplier;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.common.VersionCheck;
import com.nicolas.supplier.server.login.LoginInterface;
import com.nicolas.supplier.ui.login.LoginActivity;
import com.nicolas.toollibrary.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 200;

    private static String[] permission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.KILL_BACKGROUND_PROCESSES,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        check();
//        if (SheBangsApp.getInstance().isHaveActivity()) {
//            this.finish();
//            return;
//        }
        setContentView(R.layout.activity_first);
        requestAppPermissions();
    }


    private void check() {
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            // 如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
    }

    /**
     * 请求app所需权限
     */
    private void requestAppPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, REQUEST_CODE);
//            int checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
//                requestPermissions(permission, REQUEST_CODE);
//            } else {
//                this.jumpToMainActivity();
//            }
        } else {
            this.jumpToLoginActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isOK = true;
        if (requestCode == REQUEST_CODE) {
            //询问用户权限
//            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < permissions.length; i++) {
                isOK &= (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            if (isOK) {
                //用户同意
                this.jumpToLoginActivity();
            } else {
                //用户不同意
                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.request_power))
                        .setMessage(getResources().getString(R.string.request_power_WES))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestAppPermissions();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirstActivity.this.finish();
                            }
                        }).create().show();
            }
        }
    }

    private void jumpToLoginActivity() {
        Intent intent = new Intent(this, VersionCheckActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
