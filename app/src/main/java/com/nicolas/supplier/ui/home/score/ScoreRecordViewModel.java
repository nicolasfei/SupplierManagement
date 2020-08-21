package com.nicolas.supplier.ui.home.score;

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
import com.nicolas.supplier.data.ScoreRecord;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.score.ScoreInterface;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreRecordViewModel extends ViewModel {

    private List<ScoreRecord> scoreRecordList;
    private String recordTime;      //记录日期，区间值，不填默认当日 2019-01-01~2020-08-01
    private String scoreClassId;    //信誉分说明ID
    private String goodsId;         //新货号
    private MutableLiveData<OperateResult> scoreRecordQueryResult;
    private int currentPage = 1;
    private int pageSize = 16;
    private int pageCount = 0;       //分页查询记录

    public ScoreRecordViewModel() {
        this.scoreRecordQueryResult = new MutableLiveData<>();
        this.scoreRecordList = new ArrayList<>();
    }

    public LiveData<OperateResult> getScoreRecordQueryResult() {
        return scoreRecordQueryResult;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setScoreClassId(String scoreClassId) {
        this.scoreClassId = scoreClassId;
    }

    public String getScoreClassId() {
        return scoreClassId;
    }

    public List<ScoreRecord> getScoreRecordList() {
        return scoreRecordList;
    }

    /**
     * 查询积分记录
     */
    public void queryScoreRecord() {
        this.currentPage = 1;
        this.pageSize = 16;     //每一页16条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
    }

    /**
     * 加载更多记录
     */
    public void loadMoreScoreRecord() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            scoreRecordQueryResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;     //查询下一页
        this.query();
    }

    /**
     * 刷新记录
     */
    public void refreshScoreRecord() {
        queryScoreRecord();
    }

    /**
     * 查询
     */
    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_SCORE_RECORD;
        vo.url = ScoreInterface.ScoreRecordQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(currentPage));
        parameters.put("pageSize", String.valueOf(pageSize));
        parameters.put("pageCount", String.valueOf(pageCount));
        if (!TextUtils.isEmpty(recordTime)) {
            parameters.put("recordTime", recordTime);
        }
        if (!TextUtils.isEmpty(scoreClassId)) {
            parameters.put("scoreClassId", scoreClassId);
        }
        if (!TextUtils.isEmpty(goodsId)) {
            parameters.put("goodsId", goodsId);
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
                case ScoreInterface.ScoreRecordQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                scoreRecordList.clear();
                            }
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                                scoreRecordQueryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    ScoreRecord item = new ScoreRecord(array.getString(i));
                                    scoreRecordList.add(item);
                                }
                            }
                            pageCount = result.total;
                            scoreRecordQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            scoreRecordQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        scoreRecordQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void resetQueryCondition() {
        this.recordTime = "";      //记录日期，区间值，不填默认当日 2019-01-01~2020-08-01
        this.scoreClassId = "";    //信誉分说明ID
        this.goodsId = "";         //新货号
    }
}
