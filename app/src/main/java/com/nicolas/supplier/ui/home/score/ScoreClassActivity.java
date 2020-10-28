package com.nicolas.supplier.ui.home.score;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.ScoreClassAdapter;
import com.nicolas.supplier.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreClassActivity extends BaseActivity {

    private ScoreClassViewModel viewModel;
    private ScoreClassAdapter adapter;
    private PullRefreshListView pullToRefreshListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_explain);
        this.viewModel = new ViewModelProvider(this).get(ScoreClassViewModel.class);

        this.pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new ScoreClassAdapter(this, this.viewModel.getScoreClasses());
        this.pullToRefreshListView.setAdapter(this.adapter);
        this.pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreScoreClass();
            }
        });
        this.pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshScoreClass();
            }
        });

        /**
         * 监听查询结果
         */
        this.viewModel.getScoreClassQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(ScoreClassActivity.this, getString(R.string.failed),
                            operateResult.getError().getErrorMsg(), new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
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

        showProgressDialog( getString(R.string.querying));
        this.viewModel.queryScoreClass();       //查询
    }
}
