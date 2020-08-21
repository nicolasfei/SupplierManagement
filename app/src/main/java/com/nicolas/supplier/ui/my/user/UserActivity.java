package com.nicolas.supplier.ui.my.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierAccount;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;

public class UserActivity extends BaseActivity {

    private UserViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SupplierAccount account = SupplierKeeper.getInstance().getSupplierAccount();

        TextView supplier = findViewById(R.id.supplier);
        String supplierValue = getString(R.string.supplier) + "\u3000\u3000\u3000\u3000" + account.supplierName;
        supplier.setText(supplierValue);

        TextView name = findViewById(R.id.name);
        String nameValue = getString(R.string.name) + "\u3000\u3000\u3000\u3000\u3000" + account.name;
        name.setText(nameValue);

        TextView sex = findViewById(R.id.sex);
        String sexValue = getString(R.string.sex) + "\u3000\u3000\u3000\u3000\u3000" + account.sex;
        sex.setText(sexValue);

        TextView phone = findViewById(R.id.phone);
        String phoneValue = getString(R.string.phone) + "\u3000\u3000\u3000\u3000\u3000" + account.tel;
        phone.setText(phoneValue);

        TextView userName = findViewById(R.id.userName);
        String userNameValue = getString(R.string.userName) + "\u3000\u3000\u3000\u3000" + account.userName;
        userName.setText(userNameValue);

        TextView password = findViewById(R.id.password);
        String passwordValue = getString(R.string.password);
        password.setText(passwordValue);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BruceDialog.showPasswordModifyDialog(UserActivity.this, new BruceDialog.OnPasswordModifyListener() {
                    @Override
                    public void onPasswordModify(String old, String newPass, String newPassAgain) {
                        if ( TextUtils.isEmpty(newPass) || TextUtils.isEmpty(newPassAgain)) {   //TextUtils.isEmpty(old) ||
                            Utils.toast(UserActivity.this, R.string.inputNull);
                            return;
                        }

//                        if (!(old.equals(account.password))) {
//                            Utils.toast(UserActivity.this, R.string.oldPassError);
//                            return;
//                        }

                        if (!(newPass.equals(newPassAgain))) {
                            Utils.toast(UserActivity.this, R.string.newPassError);
                            return;
                        }

                        BruceDialog.showProgressDialog(UserActivity.this, getString(R.string.modifying));
                        viewModel.modifyPassword(newPass);
                    }
                });
            }
        });
        password.setClickable(true);

        TextView remark = findViewById(R.id.remark);
        String remarkValue = getString(R.string.remark) + "\u3000\u3000\u3000\u3000\u3000" + account.remark;
        remark.setText(remarkValue);

        /**
         * 监听密码修改结果
         */
        viewModel.getModifyPassResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    Utils.toast(UserActivity.this, R.string.modifyPassSuccess);
                }
                if (operateResult.getError() != null) {
                    Utils.toast(UserActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });
    }
}
