package com.nicolas.supplier.ui.home.salestatistics;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SaleStatisticsActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private SaleStatisticsViewModel viewModel;

    private TextView staff;     //供应商
    private TextView detailed;  //明细

    private TextView queryTime, newGoodsCode, goodsType;
    private TextView send, sale, back, purchase, surplus;           //统计汇总

    private PullRefreshListView listView;
    private SaleStatisticsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_statistics);

        this.viewModel = new ViewModelProvider(this).get(SaleStatisticsViewModel.class);
        this.drawerLayout = findViewById(R.id.drawer_layout);

        //供应商
        this.staff = findViewById(R.id.staff);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);
        //明细
        this.detailed = findViewById(R.id.detailed);
        //统计汇总
        this.send = findViewById(R.id.send);
        this.sale = findViewById(R.id.sale);
        this.back = findViewById(R.id.back);
        this.purchase = findViewById(R.id.purchase);
        this.surplus = findViewById(R.id.surplus);
        this.updateSaleStatisticsTotal();
        //查询条件
        this.queryTime = findClickView(R.id.queryTime);
        this.newGoodsCode = findClickView(R.id.newGoodsCode);
        this.goodsType = findClickView(R.id.goodsType);
        //清空条件
        findClickView(R.id.queryTimeClear);
        findClickView(R.id.newGoodsCodeClear);
        findClickView(R.id.goodsTypeClear);
        //重置
        findClickView(R.id.reset);
        findClickView(R.id.query);

        //货号统计listView
        this.listView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new SaleStatisticsAdapter(this, this.viewModel.getStatistics());
        this.listView.setAdapter(this.adapter);
        this.listView.enablePullRefresh();
        this.listView.disablePushLoadMore();
        this.listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshSaleStatistics();
            }
        });


        //监听货号统计查询结果
        this.viewModel.getSaleQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null && msg.what == 1) {     //无数据
                        dismissProgressDialog();
                        if (listView.isPullToRefreshing()) {
                            listView.refreshFinish();
                        }
                        BruceDialog.showPromptDialog(SaleStatisticsActivity.this, (String) msg.obj);
                    } else {
                        viewModel.queryStatisticsTotal();       //查询统计汇总数据
                    }
                    updateDetailed();
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    dismissProgressDialog();
                    if (listView.isPullToRefreshing()) {
                        listView.refreshFinish();
                    }
                    BruceDialog.showPromptDialog(SaleStatisticsActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //监听销售汇总查询结果
        this.viewModel.getSaleTotalQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateSaleStatisticsTotal();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(SaleStatisticsActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
                if (listView.isPullToRefreshing()) {
                    listView.refreshFinish();
                }
            }
        });

        //查询统计
        showProgressDialog(getString(R.string.querying));
        this.viewModel.querySaleStatistics();
    }

    /**
     * 更新统计汇总
     */
    private void updateSaleStatisticsTotal() {
        SaleStatisticsData total = this.viewModel.getStatisticsTotal();
        //已发货
        String sendValue = "<font color=\"black\"><big>" + getString(R.string.send) + getString(R.string.colon) + total.sendAmount +
                "件(￥</big></font><font color=\"red\"><big>" + total.sendPrice + "</big></font><font color=\"black\"><big>元)" + "</big></font>";
        this.send.setText(Html.fromHtml(sendValue, Html.FROM_HTML_MODE_COMPACT));
        //已销售
        String saleValue = "<font color=\"black\"><big>" + getString(R.string.sale) + getString(R.string.colon) + total.saleAmount +
                "件(￥</big></font><font color=\"red\"><big>" + total.salePrice + "</big></font><font color=\"black\"><big>元)" + "</big></font>";
        this.sale.setText(Html.fromHtml(saleValue, Html.FROM_HTML_MODE_COMPACT));
        //已返货
        String backValue = "<font color=\"black\"><big>" + getString(R.string.back) + getString(R.string.colon) + total.backAmount +
                "件(￥</big></font></big></font><font color=\"red\"><big>" + total.backPrice + "</big></font><font color=\"black\"><big>元)" + "</big></font>";
        this.back.setText(Html.fromHtml(backValue, Html.FROM_HTML_MODE_COMPACT));
        //实际进货
        String purchaseValue = "<font color=\"black\"><big>" + getString(R.string.purchase) + getString(R.string.colon) + total.sendAmount +
                "件(￥</big></font><font color=\"red\"><big>" + total.sendPrice + "</big></font><font color=\"black\"><big>元)" + "</big></font>";
        this.purchase.setText(Html.fromHtml(purchaseValue, Html.FROM_HTML_MODE_COMPACT));
        //剩余库存
        String surplusValue = "<font color=\"black\"><big>" + getString(R.string.surplus) + getString(R.string.colon) + total.inStockAmount +
                "件(￥</big></font><font color=\"red\"><big>" + total.inStockPrice + "</big></font><font color=\"black\"><big>元)" + "</big></font>";
        this.surplus.setText(Html.fromHtml(surplusValue, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 更新明细
     */
    private void updateDetailed() {
        String value = getString(R.string.goodsTotal) + getString(R.string.colon) + "<font color=\"black\">" +
                this.viewModel.getGoodsCount() + "</font>" + "\u3000\u3000\u3000\u3000\u3000\u3000\u3000" +
//                "<font color=\"black\"><big>" + getString(R.string.statisticsTotal) + "</big></font>" + "\u3000\u3000\u3000\u3000\u3000\u3000\u3000" +
                getString(R.string.query_date) + getString(R.string.colon) + "<font color=\"black\">" + viewModel.getQueryCondition().queryTime + "</font>";
        this.detailed.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 更新供应商
     *
     * @param staff 供应商
     */
    private void updateStaff(String staff) {
        String value = getString(R.string.staff) + "\u3000\u3000\u3000" + "<font color=\"black\">" + staff + "</font>";
        this.staff.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sale_statistics_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_screen:
                this.drawerLayout.openDrawer(GravityCompat.END);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.queryTime:            //查询时间
                String queryTime = viewModel.getQueryCondition().queryTime;
                String start = "";
                String end = "";
                if (!TextUtils.isEmpty(queryTime)) {
                    String[] times = queryTime.split("~");
                    if (times.length >= 2) {
                        start = times[0];
                        end = times[1];
                    }
                } else {
                    start = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                    end = start;
                }
                DateTimePickerDialog.showDateSlotPickerDialog(SaleStatisticsActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        updateQueryTime((start + "\u3000~\u3000" + end));
                        viewModel.getQueryCondition().setQueryTime((start + "~" + end));
                    }
                });
                break;
            case R.id.newGoodsCode:         //新货号
                BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsID, InputType.TYPE_CLASS_TEXT,
                        SaleStatisticsActivity.this, new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                updateNewGoodsCode(itemName);
                                viewModel.getQueryCondition().setNewGoodsCode(itemName);
                            }
                        });
                break;
            case R.id.goodsType:            //货号类型
                BruceDialog.showSingleChoiceDialog(R.string.goodsType, SaleStatisticsActivity.this,
                        SupplierKeeper.getInstance().getGoodsTypeShow(), new BruceDialog.OnChoiceItemListener() {
                            @Override
                            public void onChoiceItem(String itemName) {
                                updateGoodsType(itemName);
                                viewModel.getQueryCondition().setGoodsType(SupplierKeeper.getInstance().getGoodsTypeRequest(itemName));
                            }
                        });
                break;
            case R.id.queryTimeClear:       //清空查询时间--重置
                viewModel.getQueryCondition().setQueryTime("");
                updateQueryTime(viewModel.getQueryCondition().queryTime);
                break;
            case R.id.newGoodsCodeClear:    //清空新货号
                viewModel.getQueryCondition().setNewGoodsCode("");
                updateNewGoodsCode(viewModel.getQueryCondition().newGoodsCode);
                break;
            case R.id.goodsTypeClear:       //清空货号类型
                viewModel.getQueryCondition().setGoodsType("");
                updateGoodsType(viewModel.getQueryCondition().goodsType);
                break;
            case R.id.reset:                //重置
                viewModel.getQueryCondition().clear();
                updateQueryTime(viewModel.getQueryCondition().queryTime);
                updateNewGoodsCode(viewModel.getQueryCondition().newGoodsCode);
                updateGoodsType(viewModel.getQueryCondition().goodsType);
                break;
            case R.id.query:                //查询
                querySaleStatisticsForCondition();
                break;
            default:
                break;
        }
    }

    /**
     * 更新查询时间
     *
     * @param itemValue 查询时间
     */
    private void updateQueryTime(String itemValue) {
        if (!TextUtils.isEmpty(itemValue)) {
            itemValue = itemValue.replace("~", "\u3000~\u3000");
        }
        this.queryTime.setText(itemValue);
    }

    /**
     * 更新新货号
     *
     * @param itemValue 新货号
     */
    private void updateNewGoodsCode(String itemValue) {
        String value = getString(R.string.newGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        this.newGoodsCode.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 更新旧货号
     *
     * @param itemValue 旧货号
     */
    private void updateGoodsType(String itemValue) {
        String value = getString(R.string.goodsType) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        this.goodsType.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 查询货号统计数据
     */
    private void querySaleStatisticsForCondition() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        //在查询条件发生改变时，才查询
        if (viewModel.getQueryCondition().isQueryConditionUpdate()) {
            viewModel.getQueryCondition().resetQueryConditionUpdateStatus();    //查询条件是否发生改变置为false
            showProgressDialog(getString(R.string.querying));
            viewModel.querySaleStatistics();
        }
    }
}
