package com.nicolas.supplier.ui.device.printer;

import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterDevice;
import com.nicolas.printerlibraryforufovo.PrinterDeviceGroup;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;

import java.util.List;

public class PrinterViewModel extends ViewModel {
    private List<PrinterDeviceGroup> printerDeviceGroups;
    private boolean isSelfOpenOrCloseBluetooth = false;      //是否自己关闭或打开蓝牙
    private boolean isSelfStopScan = false;                  //是否自己停止蓝牙搜索

    private boolean isScanning = false;     //是否在扫描

    /**
     * 监听是否在扫描打印机
     */
    private MutableLiveData<OperateResult> isScanningPrinter;
    /**
     * 监听打印机连接状态改变
     * 连接
     * 连接中
     * 连接成功/失败
     */
    private MutableLiveData<OperateResult> printerStatusResult;
    /**
     * 监听打印机组状态改变
     * 开始扫描
     * 扫描中
     * 扫描到设备
     * 扫描完成
     * 打印机接口开/关
     */
    private MutableLiveData<OperateResult> printerGroupStatusResult;

    public PrinterViewModel() {
        printerGroupStatusResult = new MutableLiveData<>();
        printerStatusResult = new MutableLiveData<>();
        printerDeviceGroups = PrinterManager.getInstance().getPrinterGroup();
        PrinterManager.getInstance().setOnPrinterStatusUpdateListener(listener);
    }

    public LiveData<OperateResult> getPrinterGroupStatusResult() {
        return printerGroupStatusResult;
    }

    public LiveData<OperateResult> getPrinterStatusResult() {
        return printerStatusResult;
    }

    /**
     * 获取设备组
     *
     * @return 设备组
     */
    public List<PrinterDeviceGroup> getPrinterDeviceGroups() {
        return printerDeviceGroups;
    }

    /**
     * 连接打印机
     *
     * @param groupPosition 组Position
     * @param childPosition 设备Position
     */
    public void linkPrinter(int groupPosition, int childPosition) {
        PrinterManager.getInstance().linkPrinter(groupPosition, childPosition);
    }

    /**
     * 设置组接口
     *
     * @param groupPosition 组
     * @param status        接口状态
     */
    public void setPrinterGroupInterface(int groupPosition, boolean status) {
        PrinterManager.getInstance().setPrinterGroupInterface(groupPosition, status);
    }

    /**
     * 扫描打印机
     */
    public void scanPrinter() {
        PrinterManager.getInstance().scanPrinter(PrinterDevice.LINK_TYPE_BLUETOOTH);
        PrinterManager.getInstance().scanPrinter(PrinterDevice.LINK_TYPE_WIFI);
    }

    /**
     * 打印机监听
     */
    private PrinterManager.OnPrinterStatusUpdateListener listener = new PrinterManager.OnPrinterStatusUpdateListener() {

        /**
         * 打印机状态更新
         * @param groupPosition 组ID
         * @param childPosition 子ID
         * @param statusType    状态类型
         * @param change        状态类型
         */
        @Override
        public void printerStatusUpdate(int groupPosition, int childPosition, int statusType, boolean change) {
            Message msg = new Message();
            msg.what = statusType;
            msg.arg1 = groupPosition;
            msg.arg2 = childPosition;
            msg.obj = change;
            printerStatusResult.setValue(new OperateResult(new OperateInUserView(msg)));
        }

        /**
         * 打印机组的状态更新
         * @param groupPosition 组ID
         * @param statusType    状态类型
         * @param change        状态改变
         */
        @Override
        public void printerGroupStatusUpdate(int groupPosition, int statusType, boolean change) {
            //msg.arg1 打印机组ID
            //msg.what 组的某个变量的状态
            //msg.obj  状态的值
            Message msg = new Message();
            msg.arg1 = groupPosition;
            msg.what = statusType;
            msg.obj = change;
            printerGroupStatusResult.setValue(new OperateResult(new OperateInUserView(msg)));
        }
    };

    /**
     * 注销
     */
    public void destroy() {
        PrinterManager.getInstance().setOnPrinterStatusUpdateListener(null);
    }
}
