package com.nicolas.supplier.ui.my.loginrecord;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.pullrefreshlibrary.PullRefreshListView;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.SupplierAccountLoginRecordAdapter;
import com.nicolas.supplier.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginRecordActivity extends BaseActivity {

    private PullRefreshListView pullToRefreshListView;
    private SupplierAccountLoginRecordAdapter adapter;
    private LoginRecordViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_query);
        viewModel = new ViewModelProvider(this).get(LoginRecordViewModel.class);

        pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        adapter = new SupplierAccountLoginRecordAdapter(this, viewModel.getLoginRecord());
        pullToRefreshListView.setAdapter(adapter);

        pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshLoginRecord();
            }
        });

        pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadingMoreLoginRecord();
            }
        });

        /**
         * 监听查询
         */
        viewModel.getQueryLoginRecordResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        Utils.toast(LoginRecordActivity.this, (String) msg.obj);
                    }
                }
                if (operateResult.getError() != null) {
                    Utils.toast(LoginRecordActivity.this, operateResult.getError().getErrorMsg());
                }
                if (pullToRefreshListView.isPullToRefreshing()) {
                    pullToRefreshListView.refreshFinish();
                }
                if (pullToRefreshListView.isPushLoadingMore()) {
                    pullToRefreshListView.loadMoreFinish();
                }

                //更新查询日期
                String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                String queryDate = nowDate + "~" + nowDate;
                pullToRefreshListView.updateContentDate(queryDate);
            }
        });

        BruceDialog.showProgressDialog(this, this.getString(R.string.querying));
        viewModel.queryLoginRecord();
    }
}
