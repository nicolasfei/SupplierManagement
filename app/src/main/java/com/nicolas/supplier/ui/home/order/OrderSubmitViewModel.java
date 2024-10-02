package com.nicolas.supplier.ui.home.order;


import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderGoodsIDClass;
import com.nicolas.supplier.data.OrderSort;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.order.OrderInterface;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSubmitViewModel extends ViewModel {
    private List<OrderSort> sortList;               //排序选项
    private List<OrderGoodsIDClass> orderList;       //订单列表
    private OrderGoodsIDClass currentSubmit;        //当前提交订单货号

    private final int defaultPageSize = 10000;
    private int currentPage = 1;
    private int pageSize = defaultPageSize;
    private int pageCount = 0;       //分页查询记录

    private MutableLiveData<OperateResult> orderCodeQueryResult = new MutableLiveData<>();              //订单货号确认分页查询结果
    private MutableLiveData<OperateResult> orderCodeSubmitResult = new MutableLiveData<>();             //订单确认提交结果
    private MutableLiveData<OperateResult> orderCodeSortResult = new MutableLiveData<>();               //订单排序结果


    public OrderSubmitViewModel() {
        this.sortList = new ArrayList<>();
        this.sortList.add(OrderSort.SORT_OrderTime);
        this.sortList.add(OrderSort.SORT_Branch);
        this.sortList.add(OrderSort.SORT_Warehouse);
        this.sortList.add(OrderSort.SORT_OrderClass);
        this.sortList.add(OrderSort.SORT_OrderNumRise);
        this.sortList.add(OrderSort.SORT_OrderNumDrop);

        this.orderList = new ArrayList<>();
    }

    LiveData<OperateResult> getOrderQueryResult() {
        return orderCodeQueryResult;
    }

    LiveData<OperateResult> getOrderSortResult() {
        return orderCodeSortResult;
    }

    LiveData<OperateResult> getOrderSubmitResult() {
        return orderCodeSubmitResult;
    }

    List<OrderGoodsIDClass> getOrderList() {
        return orderList;
    }

    /**
     * 查询订单
     */
    void queryOrder() {
        this.currentPage = 1;
        this.pageSize = defaultPageSize;    //每一页1000条记录
        this.pageCount = 0;                 //一共多少页，默认为0
        this.query();
    }

    /**
     * 加载更多订单
     */
    void loadMoreOrders() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            orderCodeQueryResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
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
        vo.url = OrderInterface.GoodsOrderID;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
//        parameters.put("currentPage", String.valueOf(currentPage));
//        parameters.put("pageSize", String.valueOf(pageSize));
//        parameters.put("pageCount", String.valueOf(pageCount));
        vo.parameters = parameters;

        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 订单货号确认
     */
    void submitOrders(OrderGoodsIDClass order) {
        this.currentSubmit = order;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_ORDER;
        vo.url = OrderInterface.GoodsOrderSwaited;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("goodsId_Id", order.Goodsldd);
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
                case OrderInterface.GoodsOrderID:         //订单货号数据查询
                    if (result.success) {
                        try {
                            if (currentPage == 1) {                 //这个是新的查询，所以要清空之前的数据
                                orderList.clear();
                            }
                            pageCount = result.total;               //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            Message msg = new Message();
                            if (array.length() == 0) {
                                msg.obj = SupplierApp.getInstance().getString(R.string.noOrder);
                                msg.what = 1;       //表示无订单
                                orderCodeQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    //添加订单
                                    OrderGoodsIDClass item = new OrderGoodsIDClass(array.getString(i));
                                    orderList.add(item);
                                }
                                //排序，按订单状态排序，订单状态相同时按订单打印时间排序
                                sortForSwitedAndGoodsIddTime();
                                orderCodeQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            orderCodeQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        orderCodeQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case OrderInterface.GoodsOrderSwaited:            //订单确认提交到服务器
                    sortForSwitedAndGoodsIddTime();
                    if (result.success) {
                        orderCodeSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                        for (int i=0; i<orderList.size();i++){
                            if (orderList.get(i).Goodsldd.equals(currentSubmit.Goodsldd)){
                                orderList.get(i).switen=true;
                                break;
                            }
                        }
                        sortForSwitedAndGoodsIddTime();
                    } else {
                        orderCodeSortResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 排序
     *
     * 根据确认状态和订单货号ID 来排序，没有确认且ID小的排在前面
     */
    private void sortForSwitedAndGoodsIddTime() {
        Collections.sort(this.orderList, new Comparator<OrderGoodsIDClass>() {
            @Override
            public int compare(OrderGoodsIDClass o1, OrderGoodsIDClass o2) {
                if(o1.switen){
                    if (o2.switen){
                        return o1.Goodsldd.compareTo(o2.Goodsldd);
                    }else{
                        return -1;
                    }
                }else{
                    if (!o2.switen){
                        return o1.Goodsldd.compareTo(o2.Goodsldd);
                    }else {
                        return 1;
                    }
                }
            }
        });
    }

    /**
     * 排序
     *
     * @param position 排序规则
     */
    void resortOfRule(int position) {
        switch (sortList.get(position)) {
            case SORT_OrderSwited:            //订单是否确认
                Collections.sort(this.orderList, new Comparator<OrderGoodsIDClass>() {
                    @Override
                    public int compare(OrderGoodsIDClass o1, OrderGoodsIDClass o2) {
                        return o1.switen?1:(o2.switen?-1:0);
                    }
                });
                this.orderCodeSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            case SORT_OrderCodeID:               //订单货号ID
                Collections.sort(this.orderList, new Comparator<OrderGoodsIDClass>() {
                    @Override
                    public int compare(OrderGoodsIDClass o1, OrderGoodsIDClass o2) {
                        return o2.Goodsldd.compareTo(o1.Goodsldd);
                    }
                });
                this.orderCodeSortResult.setValue(new OperateResult(new OperateInUserView(null)));
                break;
            default:
                break;
        }
    }
}
