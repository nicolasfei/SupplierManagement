package com.nicolas.supplier.ui.home.order;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderClass;
import com.nicolas.supplier.data.OrderDistribution;
import com.nicolas.supplier.ui.device.printer.PrintContent;
import com.nicolas.supplier.data.OrderInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.nicolas.printerlibraryforufovo.PrinterDevice.LINK_TYPE_BLUETOOTH;

public class OrderPrintViewModel extends ViewModel {
    private ArrayList<OrderDistribution> distributions;
    private ArrayList<OrderInformation> orders;
    private ArrayList<Object> printOrders;
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

    public void setOrders(ArrayList<OrderInformation> orders, ArrayList<OrderDistribution> distributions) {
        this.orders = orders;
        this.distributions = distributions;
        this.printOrders = new ArrayList<>();
        this.goodsTotal = 0;
        this.orderTotal = 0;
        this.priceTotal = 0;

        OrderInformation lastOrder = null;
        for (OrderInformation order : this.orders) {
            this.goodsTotal += order.sendAmount;
            this.priceTotal += (order.sendAmount * order.orderPrice);
            this.orderTotal++;
            //组合订单和订单配送顺序
            if (lastOrder == null || !(order.goodsId.equals(lastOrder.goodsId))) {
                //通下单才打印配送顺序表
                if (order.orderType.getType().equals(OrderClass.ALL)) {
                    //查找此goodsId是否有配送顺序表
                    for (OrderDistribution d : this.distributions) {
                        if (d.goodsId.equals(order.goodsId)) {
                            printOrders.add(d);
                            break;
                        }
                    }
                }
            }
            //添加订单
            printOrders.add(order);
            lastOrder = order;
        }
    }

    public List<OrderInformation> getOrders() {
        return orders;
    }

    public String getTotalString() {
        return SupplierApp.getInstance().getString(R.string.ordernum) + "\u3000" + "<font color=\"green\">" + "x" + this.orderTotal + "</font>" + "\u3000\u3000" +
                SupplierApp.getInstance().getString(R.string.sendGoods) + "\u3000" + "<font color=\"green\">" + "x" + this.goodsTotal + "</font>";//+ "\u3000\u3000" +
        //SupplierApp.getInstance().getString(R.string.totalPrice) + "\u3000" + "<font color=\"red\">" + SupplierApp.getInstance().getString(R.string.money) + this.priceTotal + "</font>";
    }

    /**
     * 打印订单条码
     */
    public void printOrder() {
        int i = 1;
        List<Vector<Byte>> vectors = new ArrayList<>();
        for (Object object : this.printOrders) {
            if (object instanceof OrderInformation) {
                Log.d("TAG", "printOrder: ---------------------->");
                OrderInformation order = (OrderInformation) object;
//                try {
//                    PrinterManager.getInstance().printLabel(PrintContent.getOrderReceipt(order, i), LINK_TYPE_BLUETOOTH);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    order.printTime = "";
//                    printOrderResult.setValue(new OperateResult(new OperateError(0, SupplierApp.getInstance().getString(R.string.printer_no_link), null)));
//                    return;
//                }
                vectors.add(PrintContent.getOrderReceipt(order, i));
                i++;
            }
            if (object instanceof OrderDistribution) {
                OrderDistribution distribution = (OrderDistribution) object;
//                try {
//                    PrinterManager.getInstance().printLabel(PrintContent.getDistribution(distribution), LINK_TYPE_BLUETOOTH);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    printOrderResult.setValue(new OperateResult(new OperateError(0, SupplierApp.getInstance().getString(R.string.printer_no_link), null)));
//                    return;
//                }
                vectors.add(PrintContent.getDistribution(distribution));
            }
        }
        try {
            PrinterManager.getInstance().printLabel(vectors, LINK_TYPE_BLUETOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        PrinterManager.getInstance().printLabel(this.printOrders, LINK_TYPE_BLUETOOTH);
        printOrderResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    public void printOrderUseBlue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                try {
                    for (Object object : printOrders) {
                        if (object instanceof OrderInformation) {
                            OrderInformation order = (OrderInformation) object;
//                            if (order.sendAmount > 0) {
                            PrinterManager.getInstance().printLabelBlue(PrintContent.getOrderReceipt(order, i));
//                                PrinterManager.getInstance().printLabelBlue(new OrderView(SupplierApp.getInstance(), order, i));
                            i++;
//                            }
                        }
                        if (object instanceof OrderDistribution) {
                            OrderDistribution distribution = (OrderDistribution) object;
                            PrinterManager.getInstance().printLabelBlue(PrintContent.getDistribution(distribution));
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            printOrderResult.setValue(new OperateResult(new OperateInUserView(null)));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            printOrderResult.setValue(new OperateResult(new OperateError(-1, e.getMessage(), null)));
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * handler
     */
    private static Handler handler = new Handler();

    public String printOrderInformation() {
        StringBuilder builder = new StringBuilder();
        for (OrderInformation order : this.orders) {
            builder.append(order.toString());
        }
        return builder.toString();
    }
}
