package com.nicolas.supplier.advertisement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.advertnoticelibrary.AdvertData;
import com.nicolas.advertnoticelibrary.DataLocalManagement;
import com.nicolas.advertnoticelibrary.NoticeData;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeViewModel extends ViewModel {
    private MutableLiveData<OperateResult> noticeGetResult;
    private MutableLiveData<OperateResult> advertGetResult;
    private MutableLiveData<OperateResult> initResult;

    private List<AdvertData> advertData;
    private List<NoticeData> noticeData;

    public NoticeViewModel() {
        this.noticeGetResult = new MutableLiveData<>();
        this.advertGetResult = new MutableLiveData<>();
        this.initResult = new MutableLiveData<>();

        DataLocalManagement.getInstance().setOnManagementGetLocalDataListener(new DataLocalManagement.OnManagementGetLocalDataListener() {
            @Override
            public void onGetNoticeDataFinish(NoticeData data) {
                noticeData.add(data);
                noticeGetResult.setValue(new OperateResult(new OperateInUserView(null)));
            }

            @Override
            public void OnGetAdvertDataFinish(AdvertData data) {
                advertData.add(data);
                advertGetResult.setValue(new OperateResult(new OperateInUserView(null)));
            }
        });

        DataLocalManagement.getInstance().setOnManagementGetLocalDataInitListener(new DataLocalManagement.OnManagementGetLocalDataInitListener() {
            @Override
            public void onGetLocalDataInitFinish() {
                advertData = DataLocalManagement.getInstance().getAdvertData();
                noticeData = DataLocalManagement.getInstance().getNoticeData();
                initResult.setValue(new OperateResult(new OperateInUserView(null)));
            }
        });
    }

    public MutableLiveData<OperateResult> getAdvertGetResult() {
        return advertGetResult;
    }

    public MutableLiveData<OperateResult> getNoticeGetResult() {
        return noticeGetResult;
    }

    public MutableLiveData<OperateResult> getInitResult() {
        return initResult;
    }

    /**
     * 获取广告数据
     *
     * @return 广告数据
     */
    public List<AdvertData> getAdvertData() {
        return advertData;
    }

    /**
     * 获取通知数据
     */
    public List<NoticeData> getNoticeData() {
        return noticeData;
    }

    /**
     * 广告，通知本地初始化
     */
    public void advertNoticeLocalInit() {
        List<String> notices = new ArrayList<>();
        notices.add("1:广告位招租1");
        notices.add("2:广告位招租2");
        notices.add("3:广告位招租3");
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(SupplierApp.getInstance().getResources(), R.mipmap.advert1));
        bitmaps.add(BitmapFactory.decodeResource(SupplierApp.getInstance().getResources(), R.mipmap.advert2));
        bitmaps.add(BitmapFactory.decodeResource(SupplierApp.getInstance().getResources(), R.mipmap.advert3));
        DataLocalManagement.getInstance().initLocalData(bitmaps, notices);
    }

    /**
     * 查询广告
     */
    public void queryAdvert() {
    }

    /**
     * 查询通知
     */
    public void queryNotice() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.NoticeCheck;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 重置广告加载状态
     */
    public void resetAdvertNotice() {
        DataLocalManagement.getInstance().resetInitStatus();
    }


    /**
     * 响应
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case CommonInterface.NoticeCheck:
                    if (result.success) {
                        Message msg = new Message();
                        msg.obj = result.data;              //notice
                        noticeGetResult.setValue(new OperateResult(new OperateInUserView(msg)));
                    } else {
                        noticeGetResult.setValue(new OperateResult(new OperateError(-1, "", null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
