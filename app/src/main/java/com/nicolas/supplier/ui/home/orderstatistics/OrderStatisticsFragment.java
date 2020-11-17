package com.nicolas.supplier.ui.home.orderstatistics;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.OrderGoodsCountAdapter;
import com.nicolas.supplier.data.OrderStatistics;

import java.util.ArrayList;
import java.util.List;

public class OrderStatisticsFragment extends Fragment {
    private Context mContext;
    private OrderStatisticsFragmentViewModel viewModel;

    public static OrderStatisticsFragment newInstance(ArrayList<OrderStatistics> statistics) {
        OrderStatisticsFragment fragment = new OrderStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("statistics", statistics);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OrderStatistics> orderStatistics;
        this.viewModel = new ViewModelProvider(this).get(OrderStatisticsFragmentViewModel.class);
        if (getArguments() != null) {
            orderStatistics = getArguments().getParcelableArrayList("statistics");
            this.viewModel.setStatistics(orderStatistics);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.order_statistics_fragment, container, false);
        ListView listView = root.findViewById(R.id.listView);
        TextView noOrder = root.findViewById(R.id.noOrder);
        this.viewModel.getStatisticsSetResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    OrderGoodsCountAdapter adapter = new OrderGoodsCountAdapter(mContext, viewModel.getStatistics());
                    listView.setAdapter(adapter);
                    if (viewModel.getStatistics().size() == 0) {
                        listView.setVisibility(View.INVISIBLE);
                        noOrder.setVisibility(View.VISIBLE);
                    } else {
                        listView.setVisibility(View.VISIBLE);
                        noOrder.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        return root;
    }
}
