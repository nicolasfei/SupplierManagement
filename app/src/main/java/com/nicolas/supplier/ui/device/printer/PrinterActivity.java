package com.nicolas.supplier.ui.device.printer;

import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.nicolas.printerlibraryforufovo.PrinterDeviceGroup;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.common.PrinterListAdapter;
import com.nicolas.supplier.ui.home.score.ScoreRecordActivity;
import com.nicolas.toollibrary.BruceDialog;

public class PrinterActivity extends BaseActivity implements View.OnClickListener {

    private PrinterListAdapter listAdapter;
    private RecyclerView mRecyclerView;
    private Button search;
    private PrinterViewModel printerViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.printer_title));
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }

        printerViewModel = ViewModelProviders.of(this).get(PrinterViewModel.class);

        search = findClickView(R.id.button9);

        //适配器
        listAdapter = new PrinterListAdapter(this);
        listAdapter.setPrinterDeviceGroup(printerViewModel.getPrinterDeviceGroups());
        listAdapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition, int childPosition) {
                printerViewModel.linkPrinter(groupPosition, childPosition);      //连接设备
            }
        });
        listAdapter.setOnPrinterOperationListener(new PrinterListAdapter.OnPrinterOperationListener() {
            @Override
            public void printerPortChange(int groupPosition, boolean status) {  //设备组端口状态改变
                printerViewModel.setPrinterGroupInterface(groupPosition, status);
            }

            @Override
            public void printerChecked(int groupPosition, int childPosition) {
                //修改蓝牙名字 noting
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /**
         * 打印机状态监听
         */
        printerViewModel.getPrinterStatusResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult queryResult) {
                if (queryResult.getSuccess() != null) {
                    Message message = queryResult.getSuccess().getMessage();
//                    listAdapter.notifyChildChanged(message.arg1, message.arg2);
                    listAdapter.notifyDataChanged();
                }

                if (queryResult.getError() != null) {
                    BruceDialog.showAlertDialog(PrinterActivity.this, getString(R.string.failed),
                            queryResult.getError().getErrorMsg(), new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                    listAdapter.notifyGroupChanged((Integer) queryResult.getError().getParameter());
                }
            }
        });

        /**
         * 打印机组状态更新
         */
        printerViewModel.getPrinterGroupStatusResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message message = operateResult.getSuccess().getMessage();
                    switch (message.what) {
                        case PrinterDeviceGroup.SCAN_STATUS:
                            boolean scanning = (boolean) message.obj;
                            if (scanning) {
                                search.setClickable(false);
                                search.setTextColor(getColor(R.color.disable_gray));
                            } else {
                                search.setClickable(true);
                                search.setTextColor(getColor(android.R.color.black));
                            }
                            break;
                        default:
                            break;
                    }
                    listAdapter.notifyGroupChanged(message.arg1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button9:
                searchBluetoothDevice();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        //注销蓝牙广播
        printerViewModel.destroy();
        super.onDestroy();
    }

    /**
     * 搜索附件蓝牙设备
     */
    private void searchBluetoothDevice() {
        printerViewModel.scanPrinter();
    }
}
