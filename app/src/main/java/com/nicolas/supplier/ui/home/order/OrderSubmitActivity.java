package com.nicolas.supplier.ui.home.order;

import static com.nicolas.supplier.data.OrderStatus.SWAITED_ID;
import static com.nicolas.supplier.data.OrderStatus.SWAIT_ID;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.multileveltree.TreeNodeViewDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshMenuItem;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshSwipeMenuListView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.component.SingleFloatingDialog;
import com.nicolas.supplier.data.OrderClass;
import com.nicolas.supplier.data.OrderDataHolder;
import com.nicolas.supplier.data.OrderGoodsCountAdapter;
import com.nicolas.supplier.data.OrderGoodsIDClass;
import com.nicolas.supplier.data.OrderGoodsStatistics;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.supplier.data.OrderInformationAdapter;
import com.nicolas.supplier.data.OrderOverdue;
import com.nicolas.supplier.data.OrderQueryCondition;
import com.nicolas.supplier.data.OrderStatistics;
import com.nicolas.supplier.data.OrderStatus;
import com.nicolas.supplier.data.OrderUrgent;
import com.nicolas.supplier.data.OrderValid;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.supplier.ui.device.printer.PrinterActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderSubmitActivity extends BaseActivity {
    private TextView staff;     //供应商
    private PullRefreshSwipeMenuListView listView;
    private OrderCodeViewItemAdapter adapter;
    private OrderSubmitViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_submit);

        this.viewModel = new ViewModelProvider(this).get(OrderSubmitViewModel.class);

        //供应商
        this.staff = findViewById(R.id.staff);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        //listView
        this.listView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new OrderCodeViewItemAdapter(this, this.viewModel.getOrderList());
        this.adapter.setOnOrderCodeSubmitListener(new OrderCodeViewItemAdapter.OnOrderCodeSubmitListener() {
            @Override
            public void onOrderCodeSubmit(OrderGoodsIDClass good) {
                showProgressDialog(getString(R.string.submitting));
                viewModel.submitOrders(good);
            }
        });

        this.listView.setAdapter(this.adapter);
        this.listView.setOnLoadingMoreListener(this.loadingMoreListener);
        this.listView.setOnRefreshListener(this.refreshListener);

        //监听订单货号查询结果
        this.viewModel.getOrderQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        Toast.makeText(OrderSubmitActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(OrderSubmitActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //监听排序结果
        this.viewModel.getOrderSortResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //数据提交到服务器结果监听
        this.viewModel.getOrderSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    //更新订单状态
                    adapter.notifyDataSetChanged();
                    Toast.makeText(OrderSubmitActivity.this, "订单确认成功", Toast.LENGTH_SHORT).show();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(OrderSubmitActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //查询需要确认的订单
        showProgressDialog(getString(R.string.querying));
        viewModel.queryOrder();
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

    /**
     * 订单加载更多
     */
    private PullRefreshListView.OnLoadingMoreListener loadingMoreListener = new PullRefreshListView.OnLoadingMoreListener() {
        @Override
        public void onLoadingMore() {
            viewModel.loadMoreOrders();
        }
    };

    /**
     * 订单刷新
     */
    private PullRefreshListView.OnRefreshListener refreshListener = new PullRefreshListView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            viewModel.refreshOrders();
        }
    };

    @Override
    protected void onDestroy() {
        //清空库房树选择的状态
        SupplierKeeper.getInstance().clearStorehouseSelect();
        super.onDestroy();
    }
}
