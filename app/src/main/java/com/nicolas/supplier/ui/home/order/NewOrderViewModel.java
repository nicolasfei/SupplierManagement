package com.nicolas.supplier.ui.home.order;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderClass;
import com.nicolas.supplier.data.OrderPropertyRecord;
import com.nicolas.supplier.data.OrderStatus;
import com.nicolas.supplier.data.PrintStatus;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.order.OrderInterface;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.supplier.data.OrderQueryCondition;
import com.nicolas.supplier.data.OrderSort;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.nicolas.toollibrary.Tool.converterToFirstSpell;

public class NewOrderViewModel extends ViewModel {
    private static final String TAG = "TAG";
    private List<OrderSort> sortList;               //排序选项
    private List<OrderInformation> orderList;       //订单列表
    private int totalOrderNum;               //被选中的订单数统计
    private float totalOrderPrice;           //被选中的订单价格统计

    private OrderQueryCondition queryCondition;     //订单查询条件

    private String orderPropertyID;         //更新的订单属性ID
    private int orderPropertyNum;           //更新的订单属性的数量
    private boolean isItemCanCheck = false;     //是否在每个Item显示可以勾选的框

    private int currentPage = 1;
    private int pageSize = 16;
    private int pageCount = 0;       //分页查询记录

    private MutableLiveData<OperateResult> orderMultipleChoiceOperateResult = new MutableLiveData<>();     //多选操作结果
    private MutableLiveData<OperateResult> orderQueryResult = new MutableLiveData<>();              //订单查询结果
    private MutableLiveData<OperateResult> orderSubmitResult = new MutableLiveData<>();             //订单提交结果
    private MutableLiveData<OperateResult> orderSortResult = new MutableLiveData<>();               //订单排序结果
    private MutableLiveData<OperateResult> orderChoiceStatusChange = new MutableLiveData<>();       //订单选中状态更新结果
    private MutableLiveData<OperateResult> orderPropertyUpdateResult = new MutableLiveData<>();     //订单属性状态更新
    private boolean isManualSubmit = false;   //标志是否手动提交 
    private String manualOrderID;       //手动提交订单ID备份

    public NewOrderViewModel() {
        this.sortList = new ArrayList<>();
        this.sortList.add(OrderSort.SORT_OrderTime);
        this.sortList.add(OrderSort.SORT_Branch);
        this.sortList.add(OrderSort.SORT_Warehouse);
        this.sortList.add(OrderSort.SORT_OrderClass);
        this.sortList.add(OrderSort.SORT_OrderNumRise);
        this.sortList.add(OrderSort.SORT_OrderNumDrop);

        this.orderList = new ArrayList<>();

        this.queryCondition = new OrderQueryCondition();
        //设置默认的查询条件---近3天的统下单
        this.queryCondition.setOrderType(OrderClass.ALL);         //统下单
        //下单时间默认为近3天
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 3);
        String start = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.getTime());
        calendar.setTime(new Date());
        String end = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.getTime());
        this.queryCondition.setCreateTime((start + "~" + end));
    }

    LiveData<OperateResult> getOrderMultipleChoiceOperateResult() {
        return orderMultipleChoiceOperateResult;
    }

    LiveData<OperateResult> getOrderPropertyUpdateResult() {
        return orderPropertyUpdateResult;
    }

    LiveData<OperateResult> getOrderQueryResult() {
        return orderQueryResult;
    }

    LiveData<OperateResult> getOrderSortResult() {
        return orderSortResult;
    }

    LiveData<OperateResult> getOrderSubmitResult() {
        return orderSubmitResult;
    }

    LiveData<OperateResult> getOrderChoiceStatusChange() {
        return orderChoiceStatusChange;
    }

    List<String> getSortList() {
        List<String> sList = new ArrayList<>();
        sList.add(OrderSort.SORT_OrderTime.getRule());
//        sList.add(OrderSort.SORT_ShipmentTime.getRule());
        sList.add(OrderSort.SORT_Branch.getRule());
        sList.add(OrderSort.SORT_Warehouse.getRule());
        sList.add(OrderSort.SORT_OrderClass.getRule());
        sList.add(OrderSort.SORT_OrderNumRise.getRule());
        sList.add(OrderSort.SORT_OrderNumDrop.getRule());
        return sList;
    }

    /**
     * 清空查询条件
     */
    void clearQueryCondition() {
        this.queryCondition.clear();
        SupplierKeeper.getInstance().clearGoodsClassSelect();
        SupplierKeeper.getInstance().clearStorehouseSelect();
    }

    List<OrderInformation> getOrderList() {
        return orderList;
    }

    OrderQueryCondition getQueryCondition() {
        return queryCondition;
    }

    /**
     * 获取选中订单的商品总价
     *
     * @return 选中订单的商品总价
     */
    float getTotalPrice() {
        this.totalOrderPrice = 0;
        for (OrderInformation order : this.orderList) {
            if (order.checked) {
                this.totalOrderPrice += order.orderPrice * order.sendAmount;
            }
        }
        return this.totalOrderPrice;
    }

    /**
     * 获取选中订单数
     *
     * @return 选中订单的商品总数
     */
    int getTotalNum() {
        this.totalOrderNum = 0;
        for (OrderInformation order : this.orderList) {
            if (order.checked) {
                this.totalOrderNum++;
            }
        }
        return this.totalOrderNum;
    }

    /**
     * 查询订单
     */
    void queryOrder() {
        this.currentPage = 1;
        this.pageSize = 16;     //每一页16条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
    }

    /**
     * 加载更多订单
     */
    public void loadMoreOrders() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            orderQueryResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;     //查询下一页
        this.query();
    }

    /**
     * 刷新订单
     */
    public void refreshOrders() {
        queryOrder();
    }

    /**
     * 查询订单
     */
    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrder;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(currentPage));
        parameters.put("pageSize", String.valueOf(pageSize));
        parameters.put("pageCount", String.valueOf(pageCount));
        if (!TextUtils.isEmpty(queryCondition.getGoodsClassId())) {
            parameters.put("goodsClassId", queryCondition.getGoodsClassId());
        }
        if (!TextUtils.isEmpty(queryCondition.getOldGoodsId())) {
            parameters.put("oldGoodsId", queryCondition.getOldGoodsId());
        }
        if (!TextUtils.isEmpty(queryCondition.getGoodsId())) {
            parameters.put("goodsId", queryCondition.getGoodsId());
        }
        if (!TextUtils.isEmpty(queryCondition.getOrderType())) {
            parameters.put("orderType", queryCondition.getOrderType());
        }
        if (!TextUtils.isEmpty(queryCondition.getIsPrint())) {
            parameters.put("isPrint", queryCondition.getIsPrint());
        }
        if (!TextUtils.isEmpty(queryCondition.getInState())) {
            parameters.put("inState", queryCondition.getInState());
        }
        if (!TextUtils.isEmpty(queryCondition.getCreateTime())) {
            parameters.put("createTime", queryCondition.getCreateTime());
        }
        if (!TextUtils.isEmpty(queryCondition.getRoomReceiveTime())) {
            parameters.put("roomReceiveTime", queryCondition.getRoomReceiveTime());
        }
        if (!TextUtils.isEmpty(queryCondition.getBranchID())) {
            parameters.put("branchId", queryCondition.getBranchID());
        }
        if (!TextUtils.isEmpty(queryCondition.getStoreRoomID())) {
            parameters.put("storeRoomId", queryCondition.getStoreRoomID());
        }
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 更新订单数量
     *
     * @param propertyRecordID 订单属性
     * @param num              数量
     */
    public void updateOrderGoodsNum(String propertyRecordID, int num) {
        this.orderPropertyID = propertyRecordID;
        this.orderPropertyNum = num;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderVal;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", propertyRecordID);
        parameters.put("val", String.valueOf(num));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 批量提交订单
     */
    void submitOrders() {
        StringBuilder builder = new StringBuilder();
        for (OrderInformation order : orderList) {
            if (order.checked && order.inState.equals(OrderStatus.SWAIT)) {
                builder.append(order.id).append(",");
            }
        }
        String ids = builder.toString();
        if (TextUtils.isEmpty(ids)) {
            orderSubmitResult.setValue(new OperateResult(new OperateError(-1, SupplierApp.getInstance().getString(R.string.no_order_submit), null)));
            return;
        }

        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderPrint;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("idList", builder.toString());
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
        this.isManualSubmit = false;
    }

    /**
     * 单个手动提交订单
     *
     * @param orderID 订单ID
     */
    public void manualSubmitOrders(String orderID) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderPrint;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("idList", orderID);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
        this.isManualSubmit = true;
        this.manualOrderID = orderID;
    }

    /**
     * 按订单状态排序，订单状态相同时按订单打印时间排序
     */
    private void sortForInStateAndPrintTime() {
        //排序，已经接单排在最后面
        Collections.sort(orderList, new Comparator<OrderInformation>() {
            @Override
            public int compare(OrderInformation o1, OrderInformation o2) {
                if (o1.inState.equals(o2.inState)) {
                    return o2.printTime.compareTo(o1.printTime);
                } else {
                    return (o2.inState.equals(OrderStatus.SWAIT)) ? 1 : (o1.inState.equals(OrderStatus.SWAIT) ? -1 : 0);
                }
            }
        });
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case OrderInterface.GoodsOrder:         //订单查询
                    if (result.success) {
                        try {
                            if (currentPage == 1) {     //这个是新的查询，所以要清空之前的数据
                                orderList.clear();
                            }
                            pageCount = result.total;       //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = SupplierApp.getInstance().getString(R.string.noOrder);
                                orderQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    OrderInformation item = new OrderInformation(array.getString(i));
                                    item.canChecked = isItemCanCheck;       //设置每个项可否被勾选
                                    orderList.add(item);
                                }
                                //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                                sortForInStateAndPrintTime();
                                orderQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            orderQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        orderQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case OrderInterface.GoodsOrderPrint:    //订单提交到服务器
                    if (result.success) {
                        if (isManualSubmit) {
                            //更新订单状态
                            for (OrderInformation order : orderList) {
                                if (order.id.equals(manualOrderID)) {
                                    order.isPrint.updateStatus(PrintStatus.PRINT);
                                    order.printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
                                    order.inState = OrderStatus.SWAITED;  //供货商已接单
                                    break;
                                }
                            }
                        } else {
                            //更新订单状态
                            for (OrderInformation order : orderList) {
                                if (order.checked && order.inState.equals(OrderStatus.SWAIT)) {
                                    order.isPrint.updateStatus(PrintStatus.PRINT);
                                    order.printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
                                    order.inState = OrderStatus.SWAITED;  //供货商已接单
                                }
                            }
                        }
                        //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                        sortForInStateAndPrintTime();
                        orderSubmitResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        orderSubmitResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case OrderInterface.GoodsOrderVal:      //订单货物数量更新
                    if (result.success) {
                        boolean find = false;
                        //更新数据
                        for (OrderInformation order : orderList) {
                            for (OrderPropertyRecord record : order.propertyRecords) {
                                if (record.id.equals(orderPropertyID)) {
                                    record.actualNum = orderPropertyNum;
                                    find = true;
                                    break;
                                }
                            }
                            //重新计算订单的实际发货数量
                            if (find) {
                                int sendAmount = 0;
                                for (OrderPropertyRecord record : order.propertyRecords) {
                                    sendAmount += record.actualNum;
                                }
                                order.sendAmount = sendAmount;
                                break;
                            }
                        }
                        orderPropertyUpdateResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        orderPropertyUpdateResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置订单可多选
     */
    void setOrderMultipleChoice(boolean openOrClose) {
        for (OrderInformation information : this.orderList) {
            information.canChecked = openOrClose;
            if (!openOrClose) {              //关闭了可以多选，则之前的选择作废
                information.checked = false;
            }
        }
        this.isItemCanCheck = openOrClose;
        orderMultipleChoiceOperateResult.setValue(new OperateResult(new OperateInUserView(null)));
        if (!openOrClose) {
            this.orderChoiceStatusChange.setValue(new OperateResult(new OperateInUserView(null)));       //选中订单状态改变
        }
    }

    /**
     * 更新单个订单的状态，订单条码已打印
     *
     * @param position position
     */
    public void updateOrderStatus(int position) {
        this.orderList.get(position).isPrint.updateStatus(PrintStatus.PRINT);
    }

    /**
     * 批量更新订单状态
     */
    public void updateOrderStatusInBatch() {

    }

    /**
     * 设置是否全部选中订单
     *
     * @param isChecked true/false
     */
    void setOrdersAllChoice(boolean isChecked) {
        for (OrderInformation order : this.orderList) {
            order.checked = isChecked;
        }
        this.orderChoiceStatusChange.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 排序
     *
     * @param position 排序规则
     */
    void resortOfRule(int position) {
        switch (sortList.get(position)) {
            case SORT_OrderTime:            //下单时间来排序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        return o1.createTime.compareTo(o2.createTime);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_Branch:               //分店编号来排序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        return o2.branchId.compareTo(o1.branchId);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_Warehouse:            //库房名来排序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        String p1 = converterToFirstSpell(o1.storeRoomName);
                        String p2 = converterToFirstSpell(o2.storeRoomName);
                        return Collator.getInstance(Locale.ENGLISH).compare(p1, p2);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_OrderClass:           //订单类型来排序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        String p1 = converterToFirstSpell(o1.orderType.getType());
                        String p2 = converterToFirstSpell(o2.orderType.getType());
                        return Collator.getInstance(Locale.ENGLISH).compare(p1, p2);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_OrderNumRise:         //订单数量来排序--升序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        return Integer.compare(o1.amount, o2.amount);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_OrderNumDrop:         //订单数量来排序--降序
                Collections.sort(this.orderList, new Comparator<OrderInformation>() {
                    @Override
                    public int compare(OrderInformation o1, OrderInformation o2) {
                        return Integer.compare(o2.amount, o1.amount);
                    }
                });
                this.orderSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            default:
                break;
        }
    }

//    /**
//     * 订单被选中/取消
//     *
//     * @param information 订单
//     */
//    void setOrdersChoice(OrderInformation information) {
//        this.orderChoiceStatusChange.setValue(new OperateResult(new OperateInUserView(null)));
//    }

    /**
     * 获取已经选中的订单
     *
     * @return 选中的订单
     */
    ArrayList<OrderInformation> getCheckedOrders() {
        ArrayList<OrderInformation> checkedOrders = new ArrayList<>();
        for (OrderInformation order : this.orderList) {
            if (order.checked) {
                checkedOrders.add(order);
            }
        }
        return checkedOrders;
    }
}
