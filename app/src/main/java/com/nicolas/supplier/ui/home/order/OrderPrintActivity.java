package com.nicolas.supplier.ui.home.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.supplier.ui.device.printer.PrinterActivity;
import com.nicolas.supplier.data.OrderPrintAdapter;

/**
 * 订单打印条码页面--single
 */
public class OrderPrintActivity extends BaseActivity {
    private static final String TAG = "OrderPrintActivity";
    private OrderPrintViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_print);
        this.viewModel = new ViewModelProvider(this).get(OrderPrintViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            this.viewModel.setOrders(intent.<OrderInformation>getParcelableArrayListExtra("orders"));
        }
        //供应商
        String supplierCode = getString(R.string.supplier) + "\u3000\u3000\u3000" + SupplierKeeper.getInstance().getOnDutySupplier().name;
        TextView supplier = findViewById(R.id.supplier);
        supplier.setText(supplierCode);
        //订单
        ListView listView = findViewById(R.id.listView);
        OrderPrintAdapter adapter = new OrderPrintAdapter(this, this.viewModel.getOrders());
        listView.setAdapter(adapter);
        //合计
        TextView total = findViewById(R.id.total);
        String totalValue = getString(R.string.total) + getString(R.string.colon) + this.viewModel.getTotalString();
        total.setText(Html.fromHtml(totalValue, Html.FROM_HTML_MODE_COMPACT));
        //打印
        Button print = findViewById(R.id.button2);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printOrder();
            }
        });

        /**
         * 监听打印结果
         */
        this.viewModel.getPrintOrderResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    OrderPrintActivity.this.setResult(1);
                    showPrintSuccessDialog();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(OrderPrintActivity.this, operateResult.getError().getErrorMsg());
                    jump2PrinterActivity();
                }
            }
        });
    }

    /**
     * 订单打印成功
     */
    private void showPrintSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.print_success)
                .setMessage(R.string.print_success_back)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderPrintActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (PrinterManager.getInstance().isLinkedPrinter()) {
                this.viewModel.printOrder();
            } else {
                Utils.toast(this, getString(R.string.printer_no_link));
            }
        }
    }

    /**
     * 跳转到打印机设置页面
     */
    private void jump2PrinterActivity() {
        Intent intent = new Intent(this, PrinterActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * 打印订单
     */
    private void printOrder() {
        this.viewModel.printOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.printer_operation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_printer:
                jump2PrinterActivity();
                break;
            default:
                break;
        }
        return true;
    }
}
