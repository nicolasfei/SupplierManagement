package com.nicolas.supplier.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nicolas.supplier.R;
import com.nicolas.supplier.common.ModuleNavigation;
import com.nicolas.supplier.common.ModuleNavigationAdapter;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.my.user.UserActivity;

public class MyFragment extends Fragment implements ModuleNavigationAdapter.OnItemClickListener {

    private Context context;
    private TextView mySelf;
    private MyViewModel viewModel;
    private RecyclerView mRecyclerView;
    private ModuleNavigationAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        mySelf = root.findViewById(R.id.my);
        String value = SupplierKeeper.getInstance().getSupplierAccount().supplierName + "\n" +
                SupplierKeeper.getInstance().getSupplierAccount().name;
        mySelf.setText(value);
        mySelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                    jumpToMyInformationActivity();
                    lastClickTime = System.currentTimeMillis();
                }
            }
        });
        mySelf.setClickable(true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        //设置跨度，即时一行里面包含几个元素
        GridLayoutManager manager = new GridLayoutManager(context, 3);

        // 通过 isTitle 的标志来判断是否是 title
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewModel.getModuleNavigationList().get(position).isTitle ? 3 : 1;       //如果是标题则跨3个跨度，即标题占一行
            }
        });

        //设置item分割线
//        mRecyclerView.addItemDecoration(new RecycleGridDivider(context));
        //设置RecyclerView布局管理器
        mRecyclerView.setLayoutManager(manager);
        //设置适配器
        adapter = new ModuleNavigationAdapter(context, this);
        adapter.setContent(viewModel.getModuleNavigationList());
        mRecyclerView.setAdapter(adapter);
        /**
         * 监听按钮数据更新
         */
        viewModel.getUpdateNavNumResult().observe(requireActivity(), new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //上一次点击时间
    private static long lastClickTime = 0;
    private static final int INTERVAL_TIME = 1000;

    @Override
    public void onItemClick(int position) {
        if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
            ModuleNavigation navigation = viewModel.getModuleNavigationList().get(position);
            if (navigation.isTitle || navigation.navActivity == null) {
                return;
            }
            Intent intent = new Intent(context, navigation.navActivity);
            startActivity(intent);
            lastClickTime = System.currentTimeMillis();
        }
    }

    private void jumpToMyInformationActivity() {
        Intent intent = new Intent(this.context, UserActivity.class);
        startActivity(intent);
    }
}
