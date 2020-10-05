package com.nicolas.supplier.ui.login;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.LoginAutoMatch;
import com.nicolas.supplier.R;
import com.nicolas.supplier.MainActivity;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.Utils;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    //登陆中
    private static boolean loginIng = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppActivityManager.getInstance().addActivity(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        //初始化界面
        LoginAutoMatch.getInstance().init(this);
        final AutoCompleteTextView usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        //添加自动匹配登陆用户账号信息
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LoginAutoMatch.getInstance().getLoginUserName());
        usernameEditText.setAdapter(adapter);
        usernameEditText.setThreshold(1);   //设置输入几个字符后开始出现提示 默认是2

        //监听登陆信息输入
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

        //监听登陆结果
        loginViewModel.getLoginResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(@Nullable OperateResult loginResult) {
                BruceDialog.dismissProgressDialog();
                if (loginResult == null) {
                    Utils.toast(LoginActivity.this, R.string.login_failed);
                    loginIng = false;
                    return;
                }
                if (loginResult.getError() != null) {
                    Utils.toast(LoginActivity.this, loginResult.getError().getErrorMsg());
                    loginIng = false;
                }
                //登陆成功，获取供应商信息
                if (loginResult.getSuccess() != null) {
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.get_supplier_msg));
                    loginViewModel.getUserInformation();
                }
            }
        });

        //监听获取用户信息
        loginViewModel.getUserInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.get_supplier_account_msg));
                    loginViewModel.getUserAccountInformation();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(LoginActivity.this, getString(R.string.get_supplier_failed) + getString(R.string.colon) + operateResult.getError().getErrorMsg());
                    loginIng = false;
                }
            }
        });

        //监听获取用户账户数据
        loginViewModel.getUserAccountInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    updateUiWithUser();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(LoginActivity.this, getString(R.string.get_supplier_account_failed) + getString(R.string.colon) + operateResult.getError().getErrorMsg());
                    loginIng = false;
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
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.login_ing));
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
                if (!loginIng) {
                    loginIng = true;
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.login_ing));
                    String userName = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    loginViewModel.login(userName, password);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loginIng = false;
        }
    }

    private void updateUiWithUser() {
        String welcome = getString(R.string.welcome) + SupplierKeeper.getInstance().getOnDutySupplier().name;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //添加登陆用户
        LoginAutoMatch.getInstance().addLoginUser(SupplierKeeper.getInstance().getOnDutySupplier().userName, SupplierKeeper.getInstance().getOnDutySupplier().passWord);
        //跳转到主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        AppActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
