package com.nicolas.supplier.ui.my.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.common.OperateError;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.server.CommandResponse;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.management.ManagementInterface;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.toollibrary.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class ModifyPWViewModel extends ViewModel {

    private MutableLiveData<OperateResult> getVerificationCodeResult;       //获取验证码结果
    private MutableLiveData<OperateResult> modifyPasswordResult;            //修改密码结果
    private String setNewPass="";

    public ModifyPWViewModel() {
        this.getVerificationCodeResult = new MutableLiveData<>();
        this.modifyPasswordResult = new MutableLiveData<>();
    }

    LiveData<OperateResult> getGetVerificationCodeResult() {
        return getVerificationCodeResult;
    }

    LiveData<OperateResult> getModifyPasswordResult() {
        return modifyPasswordResult;
    }

    /**
     * 获取验证码
     *
     * @param password 验证码
     */
    void getVerificationCode(String password) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;
        vo.url = ManagementInterface.SupplierGetVerificationCode;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("password", password);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 修改密码
     *
     * @param oldPass          原始密码
     * @param newPass          新密码
     * @param newPassAgain     新密码确认
     * @param verificationCode 验证码
     */
    void modifyPassword(String oldPass, String newPass, String newPassAgain, String verificationCode) {
        this.setNewPass = newPass;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;
        vo.url = ManagementInterface.SupplierPassModify;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("oldPassword", oldPass);
        parameters.put("password", newPass);
        parameters.put("password2", newPassAgain);
        parameters.put("yzm", verificationCode);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManagementInterface.SupplierGetVerificationCode:
                    if (!result.success) {
                        getVerificationCodeResult.setValue(new OperateResult(new OperateError(-1, result.msg, null)));
                    } else {
                        getVerificationCodeResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                case ManagementInterface.SupplierPassModify:
                    if (!result.success) {
                        modifyPasswordResult.setValue(new OperateResult(new OperateError(-1, result.msg, null)));
                    } else {
                        SupplierKeeper.getInstance().getSupplierAccount().password = setNewPass;
                        modifyPasswordResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
