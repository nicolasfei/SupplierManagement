package com.nicolas.supplier.ui.home.orderstatistics;

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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderStatistics;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderStatisticsActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private OrderStatisticsViewModel viewModel;

    private TextView staff;     //供应商
    private TextView detailed;  //明细

    private TextView queryTime, newGoodsCode, oldGoodsCode;

    private OrderStatisticsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_statistics);

        this.viewModel = new ViewModelProvider(this).get(OrderStatisticsViewModel.class);
        this.drawerLayout = findViewById(R.id.drawer_layout);

        //供应商
        this.staff = findViewById(R.id.staff);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);
        //明细
        this.detailed = findViewById(R.id.detailed);
        //查询条件
        this.queryTime = findClickView(R.id.queryTime);
        this.updateQueryTime(this.viewModel.getQueryCondition().queryTime.replace("~", "\u3000~\u3000"));
        this.newGoodsCode = findClickView(R.id.newGoodsCode);
        this.oldGoodsCode = findClickView(R.id.oldGoodsCode);
        //清空条件
        findClickView(R.id.queryTimeClear);
        findClickView(R.id.newGoodsCodeClear);
        findClickView(R.id.oldGoodsCodeClear);
        //重置
        findClickView(R.id.reset);
        findClickView(R.id.query);

        //view pager
        ViewPager viewPager = findViewById(R.id.view_pager);
        Map<String, ArrayList<OrderStatistics>> map = new HashMap<>();
        map.put(getString(R.string.tab_hav_distribution), viewModel.getSendingOrders());
        map.put(getString(R.string.tab_no_receiving), viewModel.getNoReceiveOrders());
        map.put(getString(R.string.tab_had_distribution), viewModel.getBeenSendOrders());
        viewPager.setOffscreenPageLimit(0);
        this.adapter = new OrderStatisticsAdapter(this.getSupportFragmentManager(), this, map);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //监听查询结果
        this.viewModel.getOrderQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null && msg.what == 1) {     //无订单数据
                        BruceDialog.showPromptDialog(OrderStatisticsActivity.this, (String) msg.obj);
                    }
                    updateDetailed();
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(OrderStatisticsActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //查询订单
        showProgressDialog(getString(R.string.querying));
        this.viewModel.queryOrder();
    }

    /**
     * 更新明细
     */
    private void updateDetailed() {
        String value = "\u3000\u3000" + getString(R.string.orderTotal) + getString(R.string.colon) + "<font color=\"black\">" +
                this.viewModel.getOrderCount() + "</font>" + "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000" +
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
        inflater.inflate(R.menu.order_statistics_menu, menu);
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
                }
                DateTimePickerDialog.showDateSlotPickerDialog(OrderStatisticsActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        updateQueryTime((start + "\u3000~\u3000" + end));
                        viewModel.getQueryCondition().setQueryTime((start + "~" + end));
                    }
                });
                break;
            case R.id.newGoodsCode:         //新货号
                BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsID, InputType.TYPE_CLASS_TEXT,
                        OrderStatisticsActivity.this, new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                updateNewGoodsCode(itemName);
                                viewModel.getQueryCondition().setNewGoodsCode(itemName);
                            }
                        });
                break;
            case R.id.oldGoodsCode:         //旧货号
                BruceDialog.showEditInputDialog(R.string.oldGoodsID, R.string.oldGoodsID, InputType.TYPE_CLASS_TEXT,
                        OrderStatisticsActivity.this, new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                updateOldGoodsCode(itemName);
                                viewModel.getQueryCondition().setOldGoodsCode(itemName);
                            }
                        });
                break;
            case R.id.queryTimeClear:       //清空查询时间--重置
                viewModel.getQueryCondition().setQueryTime(Tool.getNearlyOneDaysDateSlot());
                updateQueryTime(viewModel.getQueryCondition().queryTime.replace("~", "\u3000~\u3000"));
                break;
            case R.id.newGoodsCodeClear:    //清空新货号
                viewModel.getQueryCondition().setNewGoodsCode("");
                updateNewGoodsCode(viewModel.getQueryCondition().newGoodsCode);
                break;
            case R.id.oldGoodsCodeClear:    //清空旧货号
                viewModel.getQueryCondition().setOldGoodsCode("");
                updateOldGoodsCode(viewModel.getQueryCondition().oldGoodsCode);
                break;
            case R.id.reset:                //重置
                viewModel.getQueryCondition().clear();
                updateQueryTime(viewModel.getQueryCondition().queryTime.replace("~", "\u3000~\u3000"));
                updateNewGoodsCode(viewModel.getQueryCondition().newGoodsCode);
                updateOldGoodsCode(viewModel.getQueryCondition().oldGoodsCode);
                break;
            case R.id.query:                //查询
                queryOrderForCondition();
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
    private void updateOldGoodsCode(String itemValue) {
        String value = getString(R.string.oldGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        this.oldGoodsCode.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 查询订单
     */
    private void queryOrderForCondition() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        //在查询条件发生改变时，才查询
        if (viewModel.getQueryCondition().isQueryConditionUpdate()) {
            viewModel.getQueryCondition().resetQueryConditionUpdateStatus();    //查询条件是否发生改变置为false
            showProgressDialog(getString(R.string.querying));
            viewModel.queryOrder();
        }
    }
}
