package com.nicolas.supplier.ui.home.order;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.ui.device.printer.PrintContent;
import com.nicolas.supplier.data.OrderInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nicolas.printerlibraryforufovo.PrinterDevice.LINK_TYPE_BLUETOOTH;

public class OrderPrintViewModel extends ViewModel {
    private static final String TAG = "OrderPrintViewModel";
    private ArrayList<OrderInformation> orders;
    private int goodsTotal;     //商品合计
    private int orderTotal;     //订单合计
    private float priceTotal;   //价格合计

    private MutableLiveData<OperateResult> printOrderResult;

    public OrderPrintViewModel() {
        this.printOrderResult = new MutableLiveData<>();
    }

    public MutableLiveData<OperateResult> getPrintOrderResult() {
        return printOrderResult;
    }

    public void setOrders(ArrayList<OrderInformation> orders) {
        this.orders = orders;
        this.goodsTotal = 0;
        this.orderTotal = 0;
        this.priceTotal = 0;
        for (OrderInformation order : this.orders) {
            this.goodsTotal += order.sendAmount;
            this.priceTotal += (order.sendAmount * order.orderPrice);
            this.orderTotal++;
            Log.d(TAG, "setOrders: " + order.goodsId + "---");//+ order.orderClass.toString());
        }
    }

    public List<OrderInformation> getOrders() {
        return orders;
    }

    public String getTotalString() {
        return SupplierApp.getInstance().getString(R.string.ordernum) + "\u3000" + "<font color=\"green\">" + "x" + this.orderTotal + "</font>" + "\u3000\u3000" +
                SupplierApp.getInstance().getString(R.string.sendGoods) + "\u3000" + "<font color=\"green\">" + "x" + this.goodsTotal + "</font>" + "\u3000\u3000" +
                SupplierApp.getInstance().getString(R.string.totalPrice) + "\u3000" + "<font color=\"red\">" + SupplierApp.getInstance().getString(R.string.money) + this.priceTotal + "</font>";
    }

    /**
     * 打印订单条码
     */
    public void printOrder() {
        for (OrderInformation order : this.orders) {
            try {
                PrinterManager.getInstance().printLabel(PrintContent.getOrderReceipt(order), LINK_TYPE_BLUETOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                order.printTime = "";
                printOrderResult.setValue(new OperateResult(new OperateError(0, SupplierApp.getInstance().getString(R.string.printer_no_link), null)));
                return;
            }
        }
        printOrderResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    public String printOrderInformation() {
        StringBuilder builder = new StringBuilder();
        for (OrderInformation order : this.orders) {
            builder.append(order.toString());
        }
        return builder.toString();
    }
}
