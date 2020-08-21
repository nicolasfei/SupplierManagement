package com.nicolas.supplier.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.toollibrary.LoginAutoMatch;
import com.nicolas.supplier.R;
import com.nicolas.supplier.MainActivity;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ProgressDialog loginDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        //开启蓝牙连接任务
        PrinterManager.getInstance().init(SupplierApp.getInstance());
        //初始化界面
        LoginAutoMatch.getInstance().init(this);
        final AutoCompleteTextView usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        //添加自动匹配登陆用户账号信息
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LoginAutoMatch.getInstance().getLoginUserName());
        usernameEditText.setAdapter(adapter);
        /**
         * 监听登陆信息输入
         */
        loginViewModel.getLoginFormState().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(@Nullable OperateResult loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                if (loginFormState.getError() != null) {
                    switch (loginFormState.getError().getErrorCode()) {
                        case -1:
                            usernameEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                        case -2:
                            passwordEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                    }
                    loginButton.setEnabled(false);
                }
                if (loginFormState.getSuccess() != null) {
                    loginButton.setEnabled(true);
                }
            }
        });

        /**
         * 监听登陆结果
         */
        loginViewModel.getLoginResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(@Nullable OperateResult loginResult) {
                dismissLoginDialog();
                if (loginResult == null) {
                    showLoginFailed(R.string.login_failed);
                    return;
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError().getErrorMsg());
                }
                //登陆成功，获取供应商信息
                if (loginResult.getSuccess() != null) {
                    showLoginDialog(getString(R.string.get_supplier_msg));
                    loginViewModel.getUserInformation();
                }
            }
        });

        /**
         * 监听获取用户信息
         */
        loginViewModel.getUserInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissLoginDialog();
                if (operateResult.getSuccess() != null) {
                    showLoginDialog(getString(R.string.get_supplier_account_msg));
                    loginViewModel.getUserAccountInformation();
                }
                if (operateResult.getError() != null) {
                    showLoginFailed(getString(R.string.get_supplier_failed) + getString(R.string.colon) + operateResult.getError().getErrorMsg());
                }
            }
        });

        /**
         * 监听获取用户账户数据
         */
        loginViewModel.getUserAccountInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissLoginDialog();
                if (operateResult.getSuccess() != null) {
                    updateUiWithUser();
                }
                if (operateResult.getError() != null) {
                    showLoginFailed(getString(R.string.get_supplier_account_failed) + getString(R.string.colon) + operateResult.getError().getErrorMsg());
                }
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showLoginDialog(getString(R.string.login_ing));
                    String userName = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    loginViewModel.login(userName, password);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                showLoginDialog(getString(R.string.login_ing));
                String userName = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginViewModel.login(userName, password);
            }
        });
    }

    private void updateUiWithUser() {
        String welcome = getString(R.string.welcome) + SupplierKeeper.getInstance().getOnDutySupplier().name;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //添加登陆用户
        LoginAutoMatch.getInstance().addLoginUser(SupplierKeeper.getInstance().getOnDutySupplier().userName, SupplierKeeper.getInstance().getOnDutySupplier().passWord);
        //跳转到主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginDialog(String loginMsg) {
        if (loginDialog == null) {
            loginDialog = new ProgressDialog(this);
            loginDialog.setCancelable(false);
            loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        loginDialog.setMessage(loginMsg);
        if (!loginDialog.isShowing()) {
            loginDialog.show();
        }
    }

    private void dismissLoginDialog() {
        if (loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        //打印机模块注销
        PrinterManager.getInstance().unManager();
        super.onDestroy();
    }
}
