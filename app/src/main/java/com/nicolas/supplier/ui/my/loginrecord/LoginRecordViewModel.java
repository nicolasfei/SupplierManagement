package com.nicolas.supplier.ui.my.loginrecord;

import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.SupplierAccountLoginRecord;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.management.ManagementInterface;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRecordViewModel extends ViewModel {

    private List<SupplierAccountLoginRecord> records;
    private MutableLiveData<OperateResult> queryLoginRecordResult;
    private int currentPage;
    private int pageSize;
    private int pageCount;       //分页查询记录

    public LoginRecordViewModel() {
        this.records = new ArrayList<>();
        this.queryLoginRecordResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getQueryLoginRecordResult() {
        return queryLoginRecordResult;
    }

    public List<SupplierAccountLoginRecord> getLoginRecord() {
        return records;
    }


    public void queryLoginRecord() {
        this.currentPage = 1;
        this.pageSize = 16;     //每一页16条记录
        this.pageCount = 0;     //一共多少也，默认为0
        this.query();
    }

    public void refreshLoginRecord() {
        queryLoginRecord();
    }

    public void loadingMoreLoginRecord() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            queryLoginRecordResult.setValue(new OperateResult(new OperateError(1, SupplierApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;     //查询下一页
        this.query();
    }

    private void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;
        vo.url = ManagementInterface.SupplierLoginQuery;
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
                case ManagementInterface.SupplierLoginQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                records.clear();
                            }
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = SupplierApp.getInstance().getString(R.string.noData);
                                queryLoginRecordResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    SupplierAccountLoginRecord item = new SupplierAccountLoginRecord(array.getString(i));
                                    records.add(item);
                                }
                            }
                            pageCount = result.total;
                            queryLoginRecordResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queryLoginRecordResult.setValue(new OperateResult(new OperateError(-1,
                                    SupplierApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        queryLoginRecordResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
