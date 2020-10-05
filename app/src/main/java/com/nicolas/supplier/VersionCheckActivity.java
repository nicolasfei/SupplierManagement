package com.nicolas.supplier;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.supplier.common.OperateResult;

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

        this.viewModel.getMatchVersionResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg.what == 1) {   //新版本发布
                        showChecking(true, getString(R.string.newVersionDowning));
                        viewModel.downNewVersionApp("http://updatesupplier.scdawn.com/supplier.apk");
                    }
                }
            }
        });

        this.viewModel.checkAppLastVersion();
    }

    private void showChecking(boolean show, String msg) {
        this.progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        this.checking.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        this.checking.setText(msg);
    }
}
