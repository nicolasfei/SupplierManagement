package com.nicolas.supplier.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;

public class SingleFloatingDialog {

    private Dialog mSortDialog;

    private Context context;
    private List<String> sortItems;
    private OnSortChoiceListener listener;

    private boolean mCanDialogShow;
    private int itemHeight;


    public SingleFloatingDialog(Context context, int itemHeight, List<String> sortItems, OnSortChoiceListener listener) {
        if (context == null || sortItems == null || sortItems.size() == 0) {
            mCanDialogShow = false;
            return;
        }
        this.context = context;
        this.itemHeight = itemHeight;
        this.sortItems = sortItems;
        this.listener = listener;

        initView();
        mCanDialogShow = true;
    }

    private void initView() {
        mSortDialog = new Dialog(context, R.style.CustomDialogSort);
        mSortDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int position = 0;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(param);
        for (String item : sortItems) {
            //TextView
            TextView itemView = new TextView(context);
            itemView.setId(position);
            itemView.setText(item);
            itemView.setGravity(Gravity.START | Gravity.CENTER);
            itemView.setTextSize(16);
            itemView.setPadding(10, 0, 0, 0);
            itemView.setBackgroundColor(Color.WHITE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnSortChoice(v.getId());
                    }
                    if (mCanDialogShow && mSortDialog.isShowing()) {
                        mSortDialog.dismiss();
                    }
                }
            });

            //分割线
            View dividingView = new View(context);
            dividingView.setBackgroundColor(Color.LTGRAY);

            layout.addView(itemView, LinearLayout.LayoutParams.MATCH_PARENT, this.itemHeight);
            layout.addView(dividingView, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            position++;
        }
        mSortDialog.setContentView(layout, new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        Window window = mSortDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    public void show() {
        if (mCanDialogShow) {
            mSortDialog.show();
        }
    }

    public boolean canShow() {
        return mCanDialogShow;
    }

    public interface OnSortChoiceListener {
        void OnSortChoice(int position);
    }
}
