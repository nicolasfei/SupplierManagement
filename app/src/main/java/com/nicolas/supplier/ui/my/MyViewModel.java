package com.nicolas.supplier.ui.my;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.R;
import com.nicolas.supplier.app.SupplierApp;
import com.nicolas.supplier.common.ModuleNavigation;
import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.ui.my.loginrecord.LoginRecordActivity;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {

    private MutableLiveData<OperateResult> updateNavNumResult;
    private List<ModuleNavigation> content = new ArrayList<>();

    public MyViewModel() {
        this.updateNavNumResult = new MutableLiveData<>();
        //content.add(new ModuleNavigation(true, getString(R.string.nav_cashier_title), 0, null));
//        content.add(new ModuleNavigation(false, SupplierApp.getInstance().getString(R.string.nav_order_account), R.drawable.ic_account, UserActivity.class));
        content.add(new ModuleNavigation(false, SupplierApp.getInstance().getString(R.string.nav_order_recode), R.drawable.ic_recode, LoginRecordActivity.class));
    }

    public LiveData<OperateResult> getUpdateNavNumResult() {
        return updateNavNumResult;
    }

    /**
     * 更新ModuleNavigation通知数字
     *
     * @param position position
     * @param num      num
     */
    public void updateModuleNoticeNum(int position, int num) {
        ModuleNavigation item = content.get(position);
        if (item.getNavigationNum() != num) {
            item.setNavigationNum(num);
            updateNavNumResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public ModuleNavigation getModuleNavigation(int position) {
        return content.get(position);
    }

    public List<ModuleNavigation> getModuleNavigationList() {
        return content;
    }
}