package com.nicolas.supplier.ui.home.score;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.ScoreClass;
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

public class ScoreClassViewModel extends ViewModel {

    private List<ScoreClass> scoreClasses;
    private MutableLiveData<OperateResult> scoreClassQueryResult;
    private int currentPage = 1;
    private int pageSize = 16;
    private int pageCount = 0;       //分页查询记录

    public ScoreClassViewModel(){
        this.scoreClasses = new ArrayList<>();
        this.scoreClassQueryResult = new MutableLiveData<>();
    }

    public List<ScoreClass> getScoreClasses() {
            return scoreClasses;
    }

    public LiveData<OperateResult> getScoreClassQueryResult() {
        return scoreClassQueryResult;
    }

    public void queryScoreClass(){
        this.currentPage = 1;
        this.pageSize = 16;     //每一页16条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
    }

    public void loadMoreScoreClass() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            scoreClassQueryResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;     //查询下一页
        this.query();
    }

    public void refreshScoreClass() {
        queryScoreClass();
    }

    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_SCORE_RECORD;
        vo.url = ScoreInterface.ScoreClassQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(currentPage));
        parameters.put("pageSize", String.valueOf(pageSize));
        parameters.put("pageCount", String.valueOf(pageCount));
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
                case ScoreInterface.ScoreClassQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                scoreClasses.clear();
                            }
                            JSONArray array = new JSONArray(result.data);
                            for (int i = 0; i < array.length(); i++) {
                                ScoreClass item = new ScoreClass(array.getString(i));
                                scoreClasses.add(item);
                            }
                            pageCount = result.total;
                            scoreClassQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            scoreClassQueryResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        scoreClassQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
