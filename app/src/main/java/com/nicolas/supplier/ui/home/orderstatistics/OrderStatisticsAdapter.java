package com.nicolas.supplier.ui.home.orderstatistics;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nicolas.supplier.R;
import com.nicolas.supplier.data.OrderStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderStatisticsAdapter extends FragmentStatePagerAdapter {

    private String[] TAB_TITLES;
    private final Context mContext;
    private Map<String, ArrayList<OrderStatistics>> statistics;

    public OrderStatisticsAdapter(@NonNull FragmentManager fm, Context mContext, Map<String, ArrayList<OrderStatistics>> statistics) {
        super(fm);
        this.mContext = mContext;
        this.statistics = statistics;
        if (this.statistics == null) {
            this.TAB_TITLES = new String[]{this.mContext.getString(R.string.tab_no_receiving),
                    this.mContext.getString(R.string.tab_hav_distribution),
                    this.mContext.getString(R.string.tab_had_distribution)};
            this.statistics = new HashMap<>();
            for (String tab : TAB_TITLES) {
                this.statistics.put(tab, null);
            }
        } else {
            this.TAB_TITLES = new String[this.statistics.size()];
            int i = 0;
            for (Map.Entry<String, ArrayList<OrderStatistics>> entry : this.statistics.entrySet()) {
                TAB_TITLES[i] = entry.getKey();
                i++;
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return OrderStatisticsFragment.newInstance(this.statistics.get(TAB_TITLES[position]));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
