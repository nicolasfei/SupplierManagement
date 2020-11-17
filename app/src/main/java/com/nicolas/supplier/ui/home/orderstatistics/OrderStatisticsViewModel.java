package com.nicolas.supplier.ui.home.orderstatistics;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.supplier.data.OrderPropertyRecord;
import com.nicolas.supplier.data.OrderStatistics;
import com.nicolas.supplier.data.OrderStatisticsProperty;
import com.nicolas.supplier.data.OrderStatus;
import com.nicolas.supplier.data.OrderValid;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.order.OrderInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatisticsViewModel extends ViewModel {
    private ArrayList<OrderInformation> orderList;                   //订单列表
    private ArrayList<OrderStatistics> noReceiveOrders;              //订单统计--未接单
    private ArrayList<OrderStatistics> sendingOrders;                //订单统计--配送中
    private ArrayList<OrderStatistics> beenSendOrders;               //订单统计--已配送
    private OrderStatisticsQueryCondition queryCondition;       //查询条件

    private final int defaultPageSize = 100000;
    private int currentPage = 1;
    private int pageSize = defaultPageSize;
    private int pageCount = 0;                                  //分页查询记录

    private MutableLiveData<OperateResult> orderQueryResult = new MutableLiveData<>();              //订单分页查询结果

    public OrderStatisticsViewModel() {
        this.orderList = new ArrayList<>();
        this.noReceiveOrders = new ArrayList<>();
        this.sendingOrders = new ArrayList<>();
        this.beenSendOrders = new ArrayList<>();
        this.queryCondition = new OrderStatisticsQueryCondition();
    }

    /**
     * 获取未接单订单的统计
     *
     * @return 订单的统计
     */
    ArrayList<OrderStatistics> getNoReceiveOrders() {
        return noReceiveOrders;
    }

    /**
     * 获取配送中订单的统计
     *
     * @return 订单的统计
     */
    ArrayList<OrderStatistics> getSendingOrders() {
        return sendingOrders;
    }

    /**
     * 获取已配送订单的统计
     *
     * @return 订单的统计
     */
    ArrayList<OrderStatistics> getBeenSendOrders() {
        return beenSendOrders;
    }

    /**
     * 获取订单查询结果订单的统计
     *
     * @return 查询结果
     */
    LiveData<OperateResult> getOrderQueryResult() {
        return orderQueryResult;
    }

    /**
     * 清空查询条件
     */
    void clearQueryCondition() {
        this.queryCondition.clear();
    }

    /**
     * 获取查询条件
     *
     * @return 查询条件
     */
    OrderStatisticsQueryCondition getQueryCondition() {
        return queryCondition;
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
        //新货号
        if (!TextUtils.isEmpty(queryCondition.newGoodsCode)) {
            parameters.put("goodsId", queryCondition.newGoodsCode);
        }
        //旧货号
        if (!TextUtils.isEmpty(queryCondition.oldGoodsCode)) {
            parameters.put("oldGoodsId", queryCondition.oldGoodsCode);
        }
        //查询日期加一天，以配合服务器
        String newDate = Tool.endDateAddOneDay(queryCondition.queryTime);
        parameters.put("createTime", newDate);
        parameters.put("valid", OrderValid.NORMAL);

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
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
                                noReceiveOrders.clear();
                                sendingOrders.clear();
                                beenSendOrders.clear();
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
                                    //统计订单数据到统计list
                                    statisticsOrderInformation(item);
                                }
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
                case OrderInterface.GoodsOrderVal:      //订单货物数量更新
                    if (result.success) {

                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 统计未接单订单信息
     *
     * @param item 订单信息
     */
    private void statisticsOrderInformation(OrderInformation item) {
        if (item == null) {
            return;
        }
        List<OrderStatistics> targetStatistics;
        switch (item.inState.getStatusID()) {
            case OrderStatus.SWAIT_ID:          //未接单
                targetStatistics = noReceiveOrders;
                break;
            case OrderStatus.SWAITED_ID:        //配送中
                targetStatistics = sendingOrders;
                break;
            case OrderStatus.ROOM_RECEIVE_ID:   //已配送
            case OrderStatus.ROOM_SEND_ID:
            case OrderStatus.BRANCH_RECEIVE_ID:
                targetStatistics = beenSendOrders;
                break;
            default:
                return;
        }

        //统计订单，并添加到统计list
        boolean findStatistics = false;
        for (OrderStatistics statistics : targetStatistics) {
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
            targetStatistics.add(statistics);
        }
    }

    /**
     * 获取查询到的订单总数
     * @return 总数
     */
    public int getOrderCount() {
        return pageCount;
    }
}
