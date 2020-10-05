package com.nicolas.supplier.advertisement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.nicolas.advertnoticelibrary.AdvertViewHolderCreator;
import com.nicolas.advertnoticelibrary.NoticeData;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

public class NoticeFragment extends Fragment {

    private NoticeViewModel noticeViewModel;
    private AdvertViewHolderCreator adapter;
    private Banner banner;
    private TextView noticeTextView;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notice, container, false);
        banner = root.findViewById(R.id.banner);
        noticeTextView = root.findViewById(R.id.notice);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeViewModel = ViewModelProviders.of(requireActivity()).get(NoticeViewModel.class);

        //广告通知加载完成
        noticeViewModel.getInitResult().observe(requireActivity(), new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult queryResult) {
                if (queryResult.getSuccess() != null) {
                    //广告轮播
                    adapter = new AdvertViewHolderCreator(noticeViewModel.getAdvertData());
                    banner.setAdapter(adapter)
                            .setIndicator(new CircleIndicator(NoticeFragment.this.context))
                            .start();

                    //通知轮播
                    List<NoticeData> notices = noticeViewModel.getNoticeData();
                    StringBuilder noticeShow = new StringBuilder();
                    for (NoticeData notice : notices) {
                        noticeShow.append(notice.getData());
                    }
                    noticeTextView.setText(noticeShow.toString());
                    noticeTextView.setSelected(true);
                }
            }
        });
        //本地数据初始化
        noticeViewModel.advertNoticeLocalInit();
    }

    @Override
    public void onDestroyView() {
        noticeViewModel.resetAdvertNotice();
        super.onDestroyView();
    }
}
