package com.nicolas.supplier.ui.home.salestatistics;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.common.CommonInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaleStatisticsViewModel extends ViewModel {

    private SaleStatisticsQueryCondition queryCondition;       //查询条件

    private SaleStatisticsData statisticsTotal;                 //统计汇总
    private ArrayList<SaleStatisticsData> statistics;                //货号统计
    private final int defaultPageSize = 100000;
    private int currentPage = 1;
    private int pageSize = defaultPageSize;
    private int pageCount = 0;                                  //分页查询记录

    private MutableLiveData<OperateResult> saleQueryResult = new MutableLiveData<>();              //销售查询结果
    private MutableLiveData<OperateResult> saleTotalQueryResult = new MutableLiveData<>();         //销售汇总查询结果

    public SaleStatisticsViewModel() {
        this.queryCondition = new SaleStatisticsQueryCondition();
        this.statisticsTotal = new SaleStatisticsData();
        this.statistics = new ArrayList<>();
    }

    public SaleStatisticsQueryCondition getQueryCondition() {
        return queryCondition;
    }

    public SaleStatisticsData getStatisticsTotal() {
        return statisticsTotal;
    }

    public ArrayList<SaleStatisticsData> getStatistics() {
        return statistics;
    }

    public MutableLiveData<OperateResult> getSaleQueryResult() {
        return saleQueryResult;
    }

    public MutableLiveData<OperateResult> getSaleTotalQueryResult() {
        return saleTotalQueryResult;
    }

    /**
     * 查询订单
     */
    void querySaleStatistics() {
        this.currentPage = 1;
        this.pageSize = 16;     //每一页10000条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.queryStatistics();
    }

    /**
     * 加载更多订单
     */
    void loadMoreSaleStatistics() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            this.saleQueryResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;     //查询下一页
        this.queryStatistics();
    }

    /**
     * 刷新货号统计
     */
    void refreshSaleStatistics() {
        querySaleStatistics();
    }

    /**
     * 货号统计查询
     */
    private void queryStatistics() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.StatisticsGoodsId;
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
        //货号类型
        if (!TextUtils.isEmpty(queryCondition.goodsType)) {
            parameters.put("goodsType", queryCondition.goodsType);
        }
        //查询日期加一天，以配合服务器
        if (!TextUtils.isEmpty(queryCondition.queryTime)) {
            parameters.put("time", Tool.endDateAddOneDay(queryCondition.queryTime));
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 货号汇总统计查询
     */
    public void queryStatisticsTotal() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.StatisticsGoodsIdTotal;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        //新货号
        if (!TextUtils.isEmpty(queryCondition.newGoodsCode)) {
            parameters.put("goodsId", queryCondition.newGoodsCode);
        }
        //货号类型
        if (!TextUtils.isEmpty(queryCondition.goodsType)) {
            parameters.put("goodsType", queryCondition.goodsType);
        }
        //查询日期加一天，以配合服务器
        if (!TextUtils.isEmpty(queryCondition.queryTime)) {
            parameters.put("time", Tool.endDateAddOneDay(queryCondition.queryTime));
        }
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
                case CommonInterface.StatisticsGoodsId:         //货号统计查询
                    if (result.success) {
                        try {
                            if (currentPage == 1) {                 //这个是新的查询，所以要清空之前的数据
                                statistics.clear();
                            }
                            pageCount = result.total;               //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            Message msg = new Message();
                            if (array.length() == 0) {
                                msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                                msg.what = 1;       //表示无数据
                                saleQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    //添加货号统计
                                    SaleStatisticsData item = new SaleStatisticsData(array.getString(i));
                                    statistics.add(item);
                                }
                                saleQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            saleQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        saleQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case CommonInterface.StatisticsGoodsIdTotal:      //货号统计汇总
                    if (result.success) {
                        statisticsTotal = new SaleStatisticsData(result.data);
                        saleTotalQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }else {
                        saleTotalQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public int getGoodsCount() {
        return pageCount;
    }
}
