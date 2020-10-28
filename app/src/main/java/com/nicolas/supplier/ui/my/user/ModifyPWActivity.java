package com.nicolas.supplier.ui.my.user;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;

public class ModifyPWActivity extends BaseActivity implements View.OnClickListener {
    private static final int VERIFICATION_COUNT = 1;            //验证码有效计时
    private static final int VERIFICATION_COUNT_DOWN = 2;       //验证码有效计时结束
    private static final int COUNT_DOWN_NUM = 5 * 60;           //5分钟

    private ModifyPWViewModel viewModel;
    private EditText verificationCode, oldPass, newPass, newPassAgain;
    private Button getVerificationCode;

    private boolean isVerificationCountDown = false;            //是否在验证码获取计时内
    private boolean outVerificationCount = false;               //退出计时

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        this.viewModel = new ViewModelProvider(this).get(ModifyPWViewModel.class);

        this.verificationCode = findViewById(R.id.verificationCode);
        this.oldPass = findViewById(R.id.oldPass);
        this.newPass = findViewById(R.id.newPass);
        this.newPassAgain = findViewById(R.id.newPassAgain);

        this.getVerificationCode = findClickView(R.id.getVerificationCode);
        findClickView(R.id.modify);

        //获取验证码结果
        this.viewModel.getGetVerificationCodeResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Utils.toast(ModifyPWActivity.this, getString(R.string.get_verification_success));
                    //显示验证有效倒计时
                    showVerificationCountDown();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(ModifyPWActivity.this, getString(R.string.warning), operateResult.getError().getErrorMsg(), null);
                }
            }
        });

        //密码修改结果
        this.viewModel.getModifyPasswordResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Utils.toast(ModifyPWActivity.this, getString(R.string.modifyPassSuccess));
                    modifyClear();
                }

                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(ModifyPWActivity.this, getString(R.string.warning), operateResult.getError().getErrorMsg(), null);
                }
                verificationCountReset();
            }
        });
    }

    /**
     * 密码修改完成后，清空
     */
    private void modifyClear() {
        this.newPass.setText("");
        this.oldPass.setText("");
        this.newPassAgain.setText("");
    }

    /**
     * 显示验证有效倒计时
     */
    private void showVerificationCountDown() {
        this.verificationCode.setText("");
        this.getVerificationCode.setEnabled(false);
        startTimerVerificationEffective();
    }

    /**
     * 验证码倒计时完成
     */
    private void verificationCountFinish() {
        this.verificationCode.setText("");
        this.getVerificationCode.setText(getString(R.string.getVerificationCode));
        this.getVerificationCode.setEnabled(true);
    }

    /**
     * 验证码倒计时重置
     */
    private void verificationCountReset() {
        outVerificationCount = true;
    }

    /**
     * 验证码有效倒计时
     */
    private void startTimerVerificationEffective() {
        new Thread(this.VerificationTimeRunnable).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getVerificationCode:
                getVerificationCode();
                break;
            case R.id.modify:
                modifyPassword();
                break;
            default:
                break;
        }
    }

    private void getVerificationCode() {
        String oldPw = oldPass.getText().toString();
        String newPw = newPass.getText().toString();
        String newPwa = newPassAgain.getText().toString();
        if ((!TextUtils.isEmpty(oldPw) && oldPw.length() >= 6) &&
                (!TextUtils.isEmpty(newPw) && newPw.length() >= 6) &&
                (!TextUtils.isEmpty(newPwa) && newPwa.length() >= 6)) {
            viewModel.getVerificationCode(newPw);
        } else {
            Utils.toast(this, getString(R.string.password_format_error));
        }
    }

    private void modifyPassword() {
        if (this.isVerificationCountDown) {
            String oldPw = oldPass.getText().toString();
            String newPw = newPass.getText().toString();
            String newPwa = newPassAgain.getText().toString();
            String yam = verificationCode.getText().toString();
            if ((!TextUtils.isEmpty(oldPw) && oldPw.length() >= 6) &&
                    (!TextUtils.isEmpty(newPw) && newPw.length() >= 6) &&
                    (!TextUtils.isEmpty(newPwa) && newPwa.length() >= 6) &&
                    (!TextUtils.isEmpty(yam) && yam.length() == 6)) {
                viewModel.modifyPassword(oldPw, newPw, newPwa, yam);
            } else {
                Utils.toast(this, getString(R.string.password_yam_format_error));
            }
        } else {
            Utils.toast(this, getString(R.string.verification_invalid));
        }
    }

    Handler handler = new Handler(SupplierApp.getInstance().getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case VERIFICATION_COUNT:        //计时验证码有效时间
                    getVerificationCode.setText(String.valueOf(msg.arg1));
                    break;
                case VERIFICATION_COUNT_DOWN:   //计时结束
                    verificationCountFinish();
                default:
                    break;
            }
        }
    };

    Runnable VerificationTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isVerificationCountDown = true;       //标记在验证码有效倒计时中
            for (int i = 0; i < COUNT_DOWN_NUM; i++) {
                //退出计时
                if (outVerificationCount) {
                    outVerificationCount = false;
                    break;
                }
                Message msg = new Message();
                msg.what = VERIFICATION_COUNT;
                msg.arg1 = COUNT_DOWN_NUM - i;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg = new Message();
            msg.what = VERIFICATION_COUNT_DOWN;
            handler.sendMessage(msg);
            isVerificationCountDown = false;       //标记在验证码已经失效
        }
    };

    @Override
    protected void onDestroy() {
        outVerificationCount = true;      //退出计时线程
        super.onDestroy();

    }
}
