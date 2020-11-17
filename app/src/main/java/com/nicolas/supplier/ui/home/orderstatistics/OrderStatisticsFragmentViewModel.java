package com.nicolas.supplier.ui.home.orderstatistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.supplier.common.OperateInUserView;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderStatistics;

import java.util.List;

public class OrderStatisticsFragmentViewModel extends ViewModel {

    private List<OrderStatistics> statistics;

    private MutableLiveData<OperateResult> statisticsSetResult = new MutableLiveData<>();

    public LiveData<OperateResult> getStatisticsSetResult() {
        return statisticsSetResult;
    }

    public void setStatistics(List<OrderStatistics> statistics) {
        this.statistics = statistics;
        this.statisticsSetResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    public List<OrderStatistics> getStatistics() {
        return statistics;
    }
}
