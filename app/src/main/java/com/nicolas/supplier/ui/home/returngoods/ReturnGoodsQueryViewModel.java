package com.nicolas.supplier.ui.home.returngoods;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.data.ReturnGoodsInformation;
import com.nicolas.supplier.data.ReturnGoodsQueryCondition;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.goods.GoodsInterface;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnGoodsQueryViewModel extends ViewModel {
    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0
    private List<ReturnGoodsInformation> returnGoodsList;
    public ReturnGoodsQueryCondition queryCondition;
    private MutableLiveData<OperateResult> returnGoodsQueryResult;

    public ReturnGoodsQueryViewModel() {
        this.returnGoodsQueryResult = new MutableLiveData<>();
        this.returnGoodsList = new ArrayList<>();
        this.queryCondition = new ReturnGoodsQueryCondition();
    }

    public LiveData<OperateResult> getReturnGoodsQueryResult() {
        return returnGoodsQueryResult;
    }

    public List<ReturnGoodsInformation> getReturnGoodsInformationList() {
        return returnGoodsList;
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        queryReturnGoods();
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            this.returnGoodsQueryResult.setValue(new OperateResult(new OperateError(-1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;
        this.query();
    }

    /**
     * 返货查询
     */
    public void queryReturnGoods() {
        this.currentPage = 1;
        this.pageSize = 64;
        this.pageCount = 0;
        this.query();
    }

    /**
     * 查询
     */
    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;
        vo.url = GoodsInterface.ReturnGoodsQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(queryCondition.goodsClassId)) {
            parameters.put("goodsClassId", queryCondition.goodsClassId);
        }
        if (!TextUtils.isEmpty(queryCondition.oldGoodsId)) {
            parameters.put("oldGoodsId", queryCondition.oldGoodsId);
        }
        if (!TextUtils.isEmpty(queryCondition.fId)) {
            parameters.put("fId", queryCondition.fId);
        }
        if (!TextUtils.isEmpty(queryCondition.goodsId)) {
            parameters.put("goodsId", queryCondition.goodsId);
        }
        if (!TextUtils.isEmpty(queryCondition.checkTime)) {
            //查询日期加一天，以配合服务器
            String newDate = Tool.endDateAddOneDay(queryCondition.checkTime);
            parameters.put("checkTime", newDate);
        }
        if (!TextUtils.isEmpty(queryCondition.barcodeID)) {
            parameters.put("barcodeId", queryCondition.barcodeID);
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
                case GoodsInterface.ReturnGoodsQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                returnGoodsList.clear();
                            }
                            pageCount = result.total;       //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                                returnGoodsQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    ReturnGoodsInformation item = new ReturnGoodsInformation(array.getString(i));
                                    returnGoodsList.add(item);
                                }
                            }
                            returnGoodsQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            returnGoodsQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        returnGoodsQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void resetQueryCondition() {
        this.queryCondition.clear();
    }

    public int getReturnGoodsCodeTotal() {
        return this.pageCount;
    }
}
