package com.nicolas.supplier.ui.home.order;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.data.OrderClass;
import com.nicolas.supplier.data.OrderDistribution;
import com.nicolas.supplier.data.OrderStatistics;
import com.nicolas.supplier.data.OrderStatisticsProperty;
import com.nicolas.supplier.data.OrderValid;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
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
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.nicolas.toollibrary.Tool.converterToFirstSpell;

public class NewOrderViewModel extends ViewModel {
    private List<OrderSort> sortList;               //排序选项
    private List<OrderInformation> orderList;       //订单列表
    private List<OrderStatistics> orderStatisticList;           //订单统计
    private List<String> orderDistributionQueryList;            //用于统计那些货号需要下载配送顺序（通下的订单）
    private ArrayList<OrderDistribution> orderDistributionList;      //订单配送顺序表
    private ArrayList<OrderInformation> printOrderList;              //待打印的订单
    private int totalOrderNum;               //被选中的订单数统计
    private float totalOrderPrice;           //被选中的订单价格统计

    private OrderQueryCondition queryCondition;     //订单查询条件

    private String orderPropertyID;         //更新的订单属性ID
    private int orderPropertyNum;           //更新的订单属性的数量
    private boolean isOrderCanSelect = true;    //是否在每个Item显示可以勾选的框--默认可以勾选
    private boolean isOrdersAllSelect = false;  //用户选择了的全部订单的选中状态，如果用户新加载了更多的数据，新加载的数据也是要处于这个状态

    private final int defaultPageSize = 10000;
    private int currentPage = 1;
    private int pageSize = defaultPageSize;
    private int pageCount = 0;       //分页查询记录

    private MutableLiveData<OperateResult> orderQueryResult = new MutableLiveData<>();              //订单分页查询结果
    private MutableLiveData<OperateResult> orderInValidResult = new MutableLiveData<>();            //订单作废
    private MutableLiveData<OperateResult> orderSubmitResult = new MutableLiveData<>();             //订单提交结果
    private MutableLiveData<OperateResult> orderSortResult = new MutableLiveData<>();               //订单排序结果
    private MutableLiveData<OperateResult> orderPullAllResult = new MutableLiveData<>();            //订单全部拉取结果
    private MutableLiveData<OperateResult> orderPropertyUpdateResult = new MutableLiveData<>();     //订单属性状态更新--暂时弃用//2020-09-04
    private MutableLiveData<OperateResult> orderCanSelectResult = new MutableLiveData<>();          //订单可以被选中
    private MutableLiveData<OperateResult> orderSelectUpdateResult = new MutableLiveData<>();       //订单选中状态更新
    private MutableLiveData<OperateResult> orderPropertyQueryResult = new MutableLiveData<>();      //订单属性查询结果
    private MutableLiveData<OperateResult> orderPropertyAllDownResult = new MutableLiveData<>();    //批量下载订单属性结果
    private MutableLiveData<OperateResult> orderDistributionQueryResult = new MutableLiveData<>();  //配送顺序查询

    private boolean isManualSubmit = false;     //标志是否手动提交
    private String manualOrderID;               //手动提交订单ID备份
    private String inValidOrderID;              //作废订单ID备份

    private String propertyQueryOrderID;        //当前查询属性的订单ID

    public NewOrderViewModel() {
        this.sortList = new ArrayList<>();
        this.sortList.add(OrderSort.SORT_OrderTime);
        this.sortList.add(OrderSort.SORT_Branch);
        this.sortList.add(OrderSort.SORT_Warehouse);
        this.sortList.add(OrderSort.SORT_OrderClass);
        this.sortList.add(OrderSort.SORT_OrderNumRise);
        this.sortList.add(OrderSort.SORT_OrderNumDrop);

        this.orderList = new ArrayList<>();
        this.orderStatisticList = new ArrayList<>();
        this.orderDistributionList = new ArrayList<>();
        this.printOrderList = new ArrayList<>();
        this.orderDistributionQueryList = new ArrayList<>();
        this.queryCondition = new OrderQueryCondition();
    }

    LiveData<OperateResult> getOrderPropertyAllDownResult() {
        return orderPropertyAllDownResult;
    }

    LiveData<OperateResult> getOrderPropertyUpdateResult() {
        return orderPropertyUpdateResult;
    }

    LiveData<OperateResult> getOrderQueryResult() {
        return orderQueryResult;
    }

    LiveData<OperateResult> getOrderDistributionQueryResult() {
        return orderDistributionQueryResult;
    }

    LiveData<OperateResult> getOrderSortResult() {
        return orderSortResult;
    }

    LiveData<OperateResult> getOrderSubmitResult() {
        return orderSubmitResult;
    }

    LiveData<OperateResult> getOrderPullAllResult() {
        return orderPullAllResult;
    }

    LiveData<OperateResult> getOrderCanSelectResult() {
        return orderCanSelectResult;
    }

    LiveData<OperateResult> getOrderInValidResult() {
        return orderInValidResult;
    }

    LiveData<OperateResult> getOrderSelectUpdateResult() {
        return orderSelectUpdateResult;
    }

    LiveData<OperateResult> getOrderPropertyQueryResult() {
        return orderPropertyQueryResult;
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
     * 获取订单统计数量
     *
     * @return 订单统计数量
     */
    public List<OrderStatistics> getOrderStatisticList() {
        return orderStatisticList;
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
            if (order.select) {
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
            if (order.select) {
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
        this.pageSize = defaultPageSize;     //每一页1000条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
    }

    /**
     * 加载更多订单
     */
    void loadMoreOrders() {
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
    void refreshOrders() {
        queryOrder();
    }

    /**
     * 查询全部订单--最大10000条
     */
    void queryAllOrder() {
        this.currentPage = 1;
        this.pageSize = 10000;     //每一页64条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
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
            //查询日期加一天，以配合服务器
            String newDate = Tool.endDateAddOneDay(queryCondition.getCreateTime());
            parameters.put("createTime", newDate);
        }
        if (!TextUtils.isEmpty(queryCondition.getRoomReceiveTime())) {
            parameters.put("roomReceiveTime", queryCondition.getRoomReceiveTime());
        }
        if (!TextUtils.isEmpty(queryCondition.getBranchID())) {
            String branchId = SupplierKeeper.getInstance().getBranchID(queryCondition.getBranchID());
            if (!TextUtils.isEmpty(branchId)) {
                parameters.put("branchId", branchId);
            }
        }
        if (!TextUtils.isEmpty(queryCondition.getStoreRoomID())) {
            parameters.put("storeRoomId", queryCondition.getStoreRoomID());
        }
        if (!TextUtils.isEmpty(queryCondition.getValid())) {
            parameters.put("valid", queryCondition.getValid());
        }
        if (!TextUtils.isEmpty(queryCondition.getOrderID())) {
            parameters.put("id", queryCondition.getOrderID());
        }
        if (!TextUtils.isEmpty(queryCondition.getIsUrgent())) {
            parameters.put("isUrgent", queryCondition.getIsUrgent());
        }
        if (!TextUtils.isEmpty(queryCondition.getOverDue())) {
            parameters.put("inValid", queryCondition.getOverDue());
        }
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询订单属性
     *
     * @param orderID 订单ID
     */
    public void queryOrderProperty(String orderID) {
        this.propertyQueryOrderID = orderID;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsPropertyOrder;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", orderID);
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
    public void updateOrderGoodsNum(String orderID, String propertyRecordID, int num) {
        this.orderPropertyID = propertyRecordID;
        this.orderPropertyNum = num;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderVal;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("orderId", orderID);
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
        if (orderList.size() == 0) {
            orderSubmitResult.setValue(new OperateResult(new OperateInUserView(null)));
            return;
        }

        if (isOrdersAllSelect) {
            submitAllOrders();
        } else {
            submitPartOrders();
        }
    }

    /**
     * 提交勾选订单
     */
    void submitPartOrders() {
        StringBuilder builder = new StringBuilder();
        for (OrderInformation order : orderList) {
            if (order.inState.getStatus().equals(OrderStatus.SWAIT) && order.select) {
                builder.append(order.id).append(",");
            }
        }
        String ids = builder.toString();
        if (TextUtils.isEmpty(ids)) {
//            Message msg = new Message();
//            msg.obj = SupplierApp.getInstance().getString(R.string.no_order_submit);
            orderSubmitResult.setValue(new OperateResult(new OperateInUserView(null)));
            return;
        }

        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderPrint;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("idList", ids.substring(0, ids.length() - 1));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
        this.isManualSubmit = false;
    }

    /**
     * 订单全选的情况下 通过查询条件提交
     */
    void submitAllOrders() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderPrintAll;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        if (!TextUtils.isEmpty(queryCondition.getStoreRoomID()))
            parameters.put("storeRoomId", queryCondition.getStoreRoomID());
        if (!TextUtils.isEmpty(queryCondition.getGoodsId()))
            parameters.put("goodsId", queryCondition.getGoodsId());
        if (!TextUtils.isEmpty(queryCondition.getIsPrint()))
            parameters.put("isPrint", queryCondition.getIsPrint());

        if (!TextUtils.isEmpty(queryCondition.getIsUrgent()))
            parameters.put("isUrgent", queryCondition.getIsUrgent());
        if (!TextUtils.isEmpty(queryCondition.getOrderID()))
            parameters.put("id", queryCondition.getOrderID());
        String newDate = Tool.endDateAddOneDay(queryCondition.getCreateTime());
        parameters.put("createTime", newDate);

        if (!TextUtils.isEmpty(queryCondition.getGoodsClassId()))
            parameters.put("goodsClassId", queryCondition.getGoodsClassId());
        if (!TextUtils.isEmpty(queryCondition.getOldGoodsId()))
            parameters.put("oldGoodsId", queryCondition.getOldGoodsId());
        if (!TextUtils.isEmpty(queryCondition.getRoomReceiveTime()))
            parameters.put("roomReceiveTime", queryCondition.getRoomReceiveTime());
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
     * 订单作废
     *
     * @param orderID orderID
     */
    public void orderInValid(String orderID) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderInValid;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        this.inValidOrderID = orderID;
        parameters.put("idList", this.inValidOrderID);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 批量作废
     */
    public void orderBatchInValid() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderInValid;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (OrderInformation order : this.orderList) {
            if (order.select) {
                builder.append(order.id).append(",");
            }
        }
        String invalids = builder.toString();
        this.inValidOrderID = invalids.substring(0, invalids.length() - 1);
        parameters.put("idList", this.inValidOrderID);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 按订单状态排序，订单状态相同时按订单打印时间排序
     */
    private void sortForInStateAndPrintTime() {
        //供应商未接单
        Comparator<OrderInformation> byInState = Comparator.comparing(OrderInformation::getInState);
        //加急订单
        Comparator<OrderInformation> byIsUrgent = Comparator.comparing(OrderInformation::getIsUrgent);
        //先按货号排序
        Comparator<OrderInformation> byGoodsID = Comparator.comparing(OrderInformation::getGoodsId);
        //库房排序
        Comparator<OrderInformation> byStoreRoom = Comparator.comparing(OrderInformation::getStoreRoomName);
        //发货数量排序--升序
        Comparator<OrderInformation> bySendAmount = Comparator.comparing(OrderInformation::getSendAmount);
        orderList.sort(byInState.thenComparing(byGoodsID).thenComparing(byStoreRoom).thenComparing(bySendAmount).thenComparing(byIsUrgent));
    }

    /**
     * 查询订单配送顺序
     */
    public void queryOrderDistribution() {
        if (currentPage == 1) {        //判断是刷新还是加载更多
            orderDistributionList.clear();
        }
        if (orderDistributionQueryList.size() == 0) {
            if (orderList.size() == 0) {
                Message msg = new Message();
                msg.obj = SupplierApp.getInstance().getString(R.string.noOrder);
                orderDistributionQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
            } else {
                orderDistributionQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
            }
            return;
        }
        new BatchQueryOrderDistribution().start();
    }

    /**
     * 获取订单是否全部被选中
     *
     * @return true/false
     */
    public boolean getIsAllOrderChecked() {
        boolean isCheckAll = true;
        for (OrderInformation order : orderList) {
            if (order.inState.getStatusID() < OrderStatus.ROOM_RECEIVE_ID && !order.select) {
                isCheckAll = false;
                break;
            }
        }
        return isCheckAll;
    }

    public void updateOrderAllSelectStatus(boolean select) {
        isOrdersAllSelect = select;
    }

    /**
     * 批量查询订单配送
     */
    private class BatchQueryOrderDistribution extends Thread {
        @Override
        public void run() {
            boolean querySuccess = true;
            String error = "";
            for (String goodsID : orderDistributionQueryList) {
                CommandVo vo = new CommandVo();
                vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
                vo.url = OrderInterface.GoodsOrderDistribution;
                vo.contentType = HttpHandler.ContentType_APP;
                vo.requestMode = HttpHandler.RequestMode_POST;
                Map<String, String> parameters = new HashMap<>();
                parameters.put("goodsId", goodsID);
                vo.parameters = parameters;
                String result = Invoker.getInstance().synchronousExec(vo);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getBoolean("success")) {
                        String dis = "", gId = "";
                        if (json.has("data")) {
                            dis = json.getString("data");
                        }
                        if (json.has("goodsId")) {
                            gId = json.getString("goodsId");
                        }
                        if (!TextUtils.isEmpty(dis) && !TextUtils.isEmpty(gId)) {
                            OrderDistribution distribution = new OrderDistribution(dis, gId);
                            orderDistributionList.add(distribution);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    querySuccess = false;
                    error = result;
                    break;
                }
            }

            if (querySuccess) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDistributionQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                });
            } else {
                String finalError = error;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDistributionQueryResult.setValue(new OperateResult(new OperateError(-1, finalError, null)));
                    }
                });
            }
        }
    }

    /**
     * 统计那些订单的货号是需要下载配送顺序表的
     *
     * @param order 订单
     */
    private void addOrderDistributionQuery(OrderInformation order) {
        boolean isFind = false;
        if (order.orderType.getType().equals(OrderClass.ALL)) {
            for (String goodsID : orderDistributionQueryList) {
                if (order.goodsId.equals(goodsID)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                orderDistributionQueryList.add(order.goodsId);
            }
        }
    }

    /**
     * 统计订单信息
     *
     * @param item 订单信息
     */
    private void statisticsOrderInformation(OrderInformation item) {
        //统计订单，并添加到统计list
        boolean findStatistics = false;
        for (OrderStatistics statistics : orderStatisticList) {
            //根据货号来，确实此订单的货号是否已经在统计数据中
            if (statistics.goodsID.equals(item.goodsId)) {
                List<OrderPropertyRecord> records = item.propertyRecords;
                List<OrderStatisticsProperty> properties = statistics.properties;
                //在属性统计中，查找OrderPropertyRecord
                for (OrderPropertyRecord r : records) {
                    boolean findRecords = false;
                    for (OrderStatisticsProperty p : properties) {
                        //此订单属性，已经存在于统计数据中，这里更新统计数据
                        if (r.color.equals(p.color) && r.size.equals(p.size)) {
                            p.num += r.actualNum;
                            findRecords = true;
                            break;
                        }
                    }
                    //此订单属性，没在统计数据中，添加新的统计数据
                    if (!findRecords) {
                        properties.add(new OrderStatisticsProperty(r.color, r.size, r.actualNum));
                    }
                }

                findStatistics = true;
                break;
            }
        }
        //此订单的货号好没有加入统计数据中
        if (!findStatistics) {
            OrderStatistics statistics = new OrderStatistics(item.goodsId, item.oldGoodsId);
            for (OrderPropertyRecord record : item.propertyRecords) {
                statistics.properties.add(new OrderStatisticsProperty(record.color, record.size, record.actualNum));
            }
            orderStatisticList.add(statistics);
        }
    }

    /**
     * 更新统计数据--重新统计所有订单中新货号=goodsID，并且颜色尺码=record的属性数据
     *
     * @param orderGoodsID 订单新货号
     * @param record       属性--颜色，尺码，数量
     */
    private void updateStatisticsOrderInformation(String orderGoodsID, OrderPropertyRecord record) {
        for (OrderStatistics statistics : orderStatisticList) {
            if (statistics.goodsID.equals(orderGoodsID)) {
                for (OrderStatisticsProperty property : statistics.properties) {
                    if (property.color.equals(record.color) && property.size.equals(record.size)) {
                        //先清零统计数据
                        property.num = 0;
                        //开始重新统计所有订单中货号=statistics.goodsID，且OrderPropertyRecord中的颜色尺码与property中的颜色尺码相同的货号属性的数量
                        for (OrderInformation order : orderList) {
                            if (order.goodsId.equals(orderGoodsID)) {
                                for (OrderPropertyRecord r : order.propertyRecords) {
                                    if (r.color.equals(record.color) && r.size.equals(record.size)) {
                                        property.num += r.actualNum;        //累加统计数据
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 订单作废后，在统计数据中减去作废的订单数据统计
     *
     * @param order 作废的订单
     */
    private void updateStatisticsForInValidOrderInformation(OrderInformation order) {
        for (OrderStatistics statistics : orderStatisticList) {
            if (order.goodsId.equals(statistics.goodsID)) {
                for (OrderPropertyRecord record : order.propertyRecords) {
                    for (OrderStatisticsProperty property : statistics.properties) {
                        if (record.color.equals(property.color) && record.size.equals(property.size)) {
                            property.num -= record.actualNum;
                            break;
                        }
                    }
                }
            }
        }
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
                            if (currentPage == 1) {                 //这个是新的查询，所以要清空之前的数据
                                orderList.clear();
                                orderStatisticList.clear();
                                orderDistributionQueryList.clear();
                                orderDistributionList.clear();
                            }
                            pageCount = result.total;               //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            Message msg = new Message();
                            if (array.length() == 0) {
                                msg.obj = SupplierApp.getInstance().getString(R.string.noOrder);
                                msg.what = 1;       //表示无订单
                                orderQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    //添加订单
                                    OrderInformation item = new OrderInformation(array.getString(i));
                                    item.canSelect = true;                      //设置每个项可否被勾选
                                    if (item.inState.getStatusID() < OrderStatus.ROOM_RECEIVE_ID) {
                                        item.select = isOrdersAllSelect;            //设置每个项的勾选状态
                                    } else {
                                        item.select = false;
                                    }
                                    orderList.add(item);
                                    statisticsOrderInformation(item);           //统计订单数据到统计list
                                    addOrderDistributionQuery(item);            //统计订单配送顺序list
                                }
                                //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                                sortForInStateAndPrintTime();
                                orderQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                            //添加订单商品统计
//                            if (!TextUtils.isEmpty(result.sta)) {
//                                addOrderStatistics(result.sta);     //添加统计
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            orderQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        orderQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case OrderInterface.GoodsOrderPrintAll:         //订单全部提交到服务器
                case OrderInterface.GoodsOrderPrint:            //订单勾选提交到服务器
                    if (result.success) {
                        if (isManualSubmit) {
                            //更新订单状态
                            for (OrderInformation order : orderList) {
                                if (order.id.equals(manualOrderID)) {
                                    order.isPrint.updateStatus(PrintStatus.PRINT);
                                    order.printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
                                    order.inState.updateStatus(OrderStatus.SWAITED);  //供货商已接单
                                    break;
                                }
                            }
                        } else {
                            //更新订单状态
                            for (OrderInformation order : orderList) {
                                if (order.select && order.inState.getStatus().equals(OrderStatus.SWAIT)) {
                                    order.isPrint.updateStatus(PrintStatus.PRINT);
                                    order.printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
                                    order.inState.updateStatus(OrderStatus.SWAITED);  //供货商已接单
//                                    order.select = false;
                                }
                            }
                        }
                        //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                        sortForInStateAndPrintTime();
                        Message msg = new Message();
                        msg.obj = result.msg;
                        orderSubmitResult.setValue(new OperateResult(new OperateInUserView(msg)));
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
                                    updateStatisticsOrderInformation(order.goodsId, record);     //更新统计数据
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
                case OrderInterface.GoodsPropertyOrder:
                    if (result.success) {
                        if (TextUtils.isEmpty(result.data) || result.data.length() < 5) {
                            Message msg = new Message();
                            msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                            orderPropertyQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            for (OrderInformation order : orderList) {
                                if (order.id.equals(propertyQueryOrderID)) {
                                    if (!order.hasQueryProperties) {
                                        order.setPropertyRecords(result.data);
                                        order.hasQueryProperties = true;    //标记该货号的属性已经查询过了
                                        order.expansion = true;             //数据加载完成，可以显示了
                                        statisticsOrderInformation(order);
                                        addOrderDistributionQuery(order);            //统计订单配送顺序list
                                    }
                                    break;
                                }
                            }
                        }
                        orderPropertyQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        orderPropertyQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case OrderInterface.GoodsOrderInValid:
                    if (result.success) {
                        //订单作废成功，在列表中更新状态
                        String[] invalids = inValidOrderID.split(",");
                        for (String invalidID : invalids) {
                            for (OrderInformation order : orderList) {
                                if (order.id.equals(invalidID)) {
                                    order.valid = OrderValid.INVALID;
                                    order.select = false;
                                    updateStatisticsForInValidOrderInformation(order);      //订单作废后，更新统计数据
                                    break;
                                }
                            }
                        }
                        //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                        sortForInStateAndPrintTime();
                        orderInValidResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        orderInValidResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 设置订单可被选中
     */
    void setOrderCanSelect(boolean openOrClose) {
        for (OrderInformation information : this.orderList) {
            information.canSelect = openOrClose;
        }
        this.isOrderCanSelect = openOrClose;
        orderCanSelectResult.setValue(new OperateResult(new OperateInUserView(null)));
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
     * 是否订单全部选中
     *
     * @return 选中
     */
    public boolean isOrdersAllChoice() {
        return isOrdersAllSelect;
    }

    /**
     * 批量下载订单属性
     */
    public void batchDownloadOrderProperty(boolean isSelect) {
        new DownOrderPropertyRecords(isSelect).start();
    }

    private static Handler handler = new Handler();

    /**
     * 同步批量下载订单属性--被选中的订单
     */
    private class DownOrderPropertyRecords extends Thread {
        private boolean isSelect = false;

        private DownOrderPropertyRecords(boolean isSelect) {
            this.isSelect = isSelect;
        }

        @Override
        public void run() {
            for (OrderInformation order : orderList) {
                if (!order.hasQueryProperties && (!isSelect || order.select)) {
                    CommandVo vo = new CommandVo();
                    vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
                    vo.url = OrderInterface.GoodsPropertyOrder;
                    vo.contentType = HttpHandler.ContentType_APP;
                    vo.requestMode = HttpHandler.RequestMode_POST;
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("id", order.id);
                    vo.parameters = parameters;
                    String property = Invoker.getInstance().synchronousExec(vo);
                    CommandResponse result = new CommandResponse(property, OrderInterface.GoodsPropertyOrder);
                    if (result.success) {
                        if (!TextUtils.isEmpty(result.data) || result.data.length() > 5) {
                            order.setPropertyRecords(result.data);
                            order.hasQueryProperties = true;    //标记该货号的属性已经查询过了
                            statisticsOrderInformation(order);      //添加货号统计
                            addOrderDistributionQuery(order);            //统计订单配送顺序list
                        }
                    }
                }
            }
            //属性下载完成，通知主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    orderPropertyAllDownResult.setValue(new OperateResult(new OperateInUserView(null)));
                }
            });
        }
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
        printOrderList.clear();
        for (OrderInformation order : this.orderList) {
            if (order.select) {
                //添加订单
                printOrderList.add(order);
            }
        }
        return printOrderList;
    }

    /**
     * 配送顺序
     *
     * @return 配送顺序
     */
    ArrayList<OrderDistribution> getOrderDistributionList() {
        return orderDistributionList;
    }

    /**
     * 通过goodsID来获取所有货号等于goodsID的订单和对应的配送顺序表
     *
     * @param goodsID goodsID
     * @return printOrderList
     */
    ArrayList<OrderInformation> getOrdersFormGoodsID(String goodsID) {
        printOrderList.clear();
        for (OrderInformation order : this.orderList) {
            if (order.goodsId.equals(goodsID)) {
                printOrderList.add(order);
            }
        }
        return printOrderList;
    }

    /**
     * 获取当前查询条件下的总订单数
     *
     * @return 总订单数
     */
    int getOrderTotal() {
        return this.pageCount;
    }

    /**
     * 设置全部订单的选中状态
     */
    void setOrderAllSelectStatus(boolean select) {
        isOrdersAllSelect = select;
        for (OrderInformation order : orderList) {
            if (order.inState.getStatusID() < OrderStatus.ROOM_RECEIVE_ID) {        //只有库房未收货的单子才能被选中，进行操作
                order.select = select;
            }
        }
        orderSelectUpdateResult.setValue(new OperateResult(new OperateInUserView(null)));
    }
}
