package com.nicolas.supplier.common;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nicolas.supplier.R;

import java.util.List;

public class ModuleNavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NavigationAdapter";

    private Context context;
    private List<ModuleNavigation> content;
    private OnItemClickListener mListener;

    public ModuleNavigationAdapter(Context context, OnItemClickListener mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    public void setContent(List<ModuleNavigation> content) {
        this.content = content;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {     //标题
            return new NavTitleHolder(LayoutInflater.from(context).inflate(R.layout.navigation_title, parent, false));
        } else {
            return new NavButtonHolder(LayoutInflater.from(context).inflate(R.layout.navigation_button, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ModuleNavigation item = content.get(position);
        //判断是否是标题item
        if (holder instanceof NavTitleHolder) {
            ((NavTitleHolder) holder).navigationTitle.setText(item.name);
        } else if (holder instanceof NavButtonHolder) {
            ((NavButtonHolder) holder).navigationName.setText(item.name);
            ((NavButtonHolder) holder).notificationButton.setBackground(context.getDrawable(item.bitmapID));
//            ((NavButtonHolder) holder).notificationButton.setNotificationNumber(item.getNavigationNum());
            //设置监听
            ((NavButtonHolder) holder).notificationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: onItemClick " + position);
                    mListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return content.get(position).isTitle ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return content == null ? 0 : content.size();
    }

    public class NavButtonHolder extends RecyclerView.ViewHolder {
        private Button notificationButton;
        private TextView navigationName;

        public NavButtonHolder(@NonNull View itemView) {
            super(itemView);
            notificationButton = itemView.findViewById(R.id.notificationButton);
            navigationName = itemView.findViewById(R.id.navigationName);
        }
    }

    public class NavTitleHolder extends RecyclerView.ViewHolder {
        private TextView navigationTitle;

        public NavTitleHolder(@NonNull View itemView) {
            super(itemView);
            navigationTitle = itemView.findViewById(R.id.navigationTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
