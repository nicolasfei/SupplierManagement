package com.nicolas.supplier.data;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.supplier.R;

import java.util.List;


public class ScoreClassAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScoreClass> scoreClasses;

    public ScoreClassAdapter(Context context, List<ScoreClass> scoreClassList) {
        this.mContext = context;
        this.scoreClasses = scoreClassList;
    }

    @Override
    public int getCount() {
        return this.scoreClasses == null ? 0 : this.scoreClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return this.scoreClasses == null ? null : this.scoreClasses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.score_explain_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ScoreClass scoreClass = scoreClasses.get(position);

        String ruleValue = mContext.getString(R.string.scoreRule) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreClass.name + "</font>";
        holder.rule.setText(Html.fromHtml(ruleValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = mContext.getString(R.string.remark) + mContext.getString(R.string.colon) +"<font color=\"black\">" + scoreClass.remark + "</font>";
        holder.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    private static class ViewHolder {
        private TextView rule;
        private TextView remark;

        private ViewHolder(View root) {
            this.rule = root.findViewById(R.id.rule);
            this.remark = root.findViewById(R.id.remark);
        }
    }
}
