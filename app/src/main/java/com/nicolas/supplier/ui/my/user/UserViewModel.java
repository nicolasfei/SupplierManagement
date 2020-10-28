package com.nicolas.supplier.ui.my.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.management.ManagementInterface;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private String newPass;
    private MutableLiveData<OperateResult> modifyPassResult;

    public UserViewModel() {
        this.modifyPassResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getModifyPassResult() {
        return modifyPassResult;
    }

    public void modifyPassword(String newPass) {
        this.newPass = newPass;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;
        vo.url = ManagementInterface.SupplierPassModify;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("password", newPass);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManagementInterface.SupplierPassModify:
                    if (!result.success) {
                        modifyPassResult.setValue(new OperateResult(new OperateError(-1, result.msg, null)));
                    } else {
                        SupplierKeeper.getInstance().getSupplierAccount().password = newPass;
                        modifyPassResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
