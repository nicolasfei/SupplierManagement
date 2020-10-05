package com.nicolas.supplier.ui.home.goods;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.GoodsCode;
import com.nicolas.supplier.data.GoodsCodeQueryCondition;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.goods.GoodsInterface;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nicolas.supplier.server.goods.GoodsInterface.GoodsPropertyStock;

public class GoodsQueryViewModel extends ViewModel {

    //查询条件
    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0 --- 当前查询条件下的订单总数
    public GoodsCodeQueryCondition queryCondition;      //查询条件

    private String updateGoodsID;           //更新货号ID
    private String updateGoodsPropertyID;   //货号属性ID
    private String updateStock;             //更新Stock值
    private List<GoodsCode> goodsCodeList;

    private String propertyQueryGoodsCodeID;    //当前查询属性的货号ID

    private MutableLiveData<OperateResult> queryGoodsIDResult;
    private MutableLiveData<OperateResult> queryGoodsPropertyResult;
    private MutableLiveData<OperateResult> updateGoodsStatusResult;

    public GoodsQueryViewModel() {
        this.queryGoodsIDResult = new MutableLiveData<>();
        this.queryGoodsPropertyResult = new MutableLiveData<>();
        this.updateGoodsStatusResult = new MutableLiveData<>();
        this.goodsCodeList = new ArrayList<>();
        this.queryCondition = new GoodsCodeQueryCondition();
    }

    public LiveData<OperateResult> getQueryGoodsIDResult() {
        return queryGoodsIDResult;
    }

    public LiveData<OperateResult> getQueryGoodsPropertyResult() {
        return queryGoodsPropertyResult;
    }

    public LiveData<OperateResult> getUpdateGoodsStatusResult() {
        return updateGoodsStatusResult;
    }

    public List<GoodsCode> getGoodsCodeList() {
        return goodsCodeList;
    }

    /**
     * 货号查询
     */
    public void queryGoodsID() {
        this.currentPage = 1;
        this.pageSize = 16;
        this.pageCount = 0;
        this.query();
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        queryGoodsID();
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            this.queryGoodsIDResult.setValue(new OperateResult(new OperateError(-1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;
        this.query();
    }

    /**
     * 货号查询
     */
    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;
        vo.url = GoodsInterface.GoodsIDQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(queryCondition.getGoodsClassId())) {
            parameters.put("goodsClassId", queryCondition.getGoodsClassId());
        }
        if (!TextUtils.isEmpty(queryCondition.getCreateTime())) {
            parameters.put("createTime", queryCondition.getCreateTime());
        }
        if (!TextUtils.isEmpty(queryCondition.getGoodsId())) {
            parameters.put("goodsId", queryCondition.getGoodsId());
        }
        if (!TextUtils.isEmpty(queryCondition.getGoodsType())) {
            parameters.put("goodsType", queryCondition.getGoodsType());
        }
        if (!TextUtils.isEmpty(queryCondition.getIsStock())) {
            parameters.put("isStock", queryCondition.getIsStock());
        }
        if (!TextUtils.isEmpty(queryCondition.getOldGoodsId())) {
            parameters.put("oldGoodsId", queryCondition.getOldGoodsId());
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询货号属性
     *
     * @param goodsCodeID 货号ID
     */
    public void queryGoodsCodeProperty(String goodsCodeID) {
        this.propertyQueryGoodsCodeID = goodsCodeID;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;
        vo.url = GoodsInterface.GoodsPropertyQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsCodeID);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 设置货号无货
     *
     * @param goodsID 货号ID
     * @param stock   是否允许下单(allow允许/forbid禁止)
     */
    public void updateGoodsStatus(String goodsID, String stock) {
        this.updateGoodsID = goodsID;
        this.updateGoodsPropertyID = "";
        this.updateStock = stock;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;
        vo.url = GoodsInterface.GoodsStock;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsID);
        parameters.put("isStock", stock);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 设置货号属性无货
     *
     * @param goodsID             货号ID
     * @param goodsCodePropertyID 货号属性ID
     * @param stock               是否允许下单(allow允许/forbid禁止)
     */
    public void updateGoodsPropertyStatus(String goodsID, String goodsCodePropertyID, String stock) {
        this.updateGoodsID = goodsID;
        this.updateGoodsPropertyID = goodsCodePropertyID;
        this.updateStock = stock;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;
        vo.url = GoodsPropertyStock;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsCodePropertyID);
        parameters.put("isStock", stock);
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
                case GoodsInterface.GoodsIDQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                goodsCodeList.clear();
                            }
                            pageCount = result.total;       //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                                queryGoodsIDResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    GoodsCode item = new GoodsCode(array.getString(i));
                                    goodsCodeList.add(item);
                                }
                                queryGoodsIDResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queryGoodsIDResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        queryGoodsIDResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case GoodsInterface.GoodsPropertyQuery:
                    if (result.success) {
                        if (TextUtils.isEmpty(result.data) || result.data.length() < 5) {
                            Message msg = new Message();
                            msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                            queryGoodsPropertyResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            for (GoodsCode good : goodsCodeList) {
                                if (good.id.equals(propertyQueryGoodsCodeID)) {
                                    if (!good.hasQueryProperties) {
                                        good.setProperties(result.data);
                                        good.showProperties = true;         //数据加载完成，可以显示了
                                        good.hasQueryProperties = true;     //标记该货号的属性已经查询过了
                                    }
                                    break;
                                }
                            }
                            queryGoodsPropertyResult.setValue(new OperateResult(new OperateInUserView(null)));
                        }
                    } else {
                        queryGoodsPropertyResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case GoodsInterface.GoodsStock:
                    if (result.success) {
                        //更新货号stock
                        for (GoodsCode goodsCode : goodsCodeList) {
                            if (goodsCode.id.equals(updateGoodsID)) {
                                goodsCode.isStock = updateStock.equals("allow") ? (SupplierApp.getInstance().getString(R.string.stock)) :
                                        (SupplierApp.getInstance().getString(R.string.noStock));
                                break;
                            }
                        }
                        updateGoodsStatusResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        updateGoodsStatusResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case GoodsInterface.GoodsPropertyStock:
                    if (result.success) {
                        //更新货号属性stock
                        for (GoodsCode goodsCode : goodsCodeList) {
                            if (goodsCode.id.equals(updateGoodsID)) {
                                for (GoodsCode.Property property : goodsCode.properties) {
                                    if (property.id.equals(updateGoodsPropertyID)) {
                                        property.isStock = updateStock.equals("allow") ? (SupplierApp.getInstance().getString(R.string.stock)) :
                                                (SupplierApp.getInstance().getString(R.string.noStock));
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        updateGoodsStatusResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        updateGoodsStatusResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取当前查询条件下的总货号数
     *
     * @return 总订单数
     */
    public int getGoodsCodeTotal() {
        return this.pageCount;
    }
}
