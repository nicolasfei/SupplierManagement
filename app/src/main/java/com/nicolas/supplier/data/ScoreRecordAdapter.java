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

public class ScoreRecordAdapter extends BaseAdapter {

    private List<ScoreRecord> scoreRecordList;
    private Context mContext;

    public ScoreRecordAdapter(Context context, List<ScoreRecord> scoreRecords) {
        this.scoreRecordList = scoreRecords;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.scoreRecordList == null ? 0 : this.scoreRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.scoreRecordList == null ? null : this.scoreRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.score_record_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ScoreRecord scoreRecord = scoreRecordList.get(position);

        String supplierNameValue = mContext.getString(R.string.supplier) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.supplierName + "</font>";
        holder.supplier.setText(Html.fromHtml(supplierNameValue, Html.FROM_HTML_MODE_COMPACT));

        String validValue = "<font color=\"black\">" + scoreRecord.valid + "</font>";
        holder.valid.setText(Html.fromHtml(validValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsID) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.goodsId + "</font>";
        holder.goodsId.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String scoreClassNameValue = mContext.getString(R.string.scoreClassName) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.scoreClassName + "</font>";
        holder.scoreClassName.setText(Html.fromHtml(scoreClassNameValue, Html.FROM_HTML_MODE_COMPACT));

        String valValue = mContext.getString(R.string.val) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.val + "</font>";
        holder.val.setText(Html.fromHtml(valValue, Html.FROM_HTML_MODE_COMPACT));

        String balanceValue = mContext.getString(R.string.balance) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.balance + "</font>";
        holder.balance.setText(Html.fromHtml(balanceValue, Html.FROM_HTML_MODE_COMPACT));

        String recordTimeValue = mContext.getString(R.string.recordTime) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.recordTime + "</font>";
        holder.recordTime.setText(Html.fromHtml(recordTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String opTimeValue = mContext.getString(R.string.opTime) + mContext.getString(R.string.colon) + "<font color=\"black\">" + scoreRecord.opTime + "</font>";
        holder.opTime.setText(Html.fromHtml(opTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = "<font color=\"black\"><big>" + mContext.getString(R.string.remark) + "</big></font>" + mContext.getString(R.string.colon)  + scoreRecord.remark;
        holder.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    private static class ViewHolder {
        private TextView supplier, valid, goodsId, scoreClassName, val, balance, recordTime, opTime, remark;

        private ViewHolder(View root) {
            this.supplier = root.findViewById(R.id.supplier);
            this.valid = root.findViewById(R.id.valid);
            this.goodsId = root.findViewById(R.id.goodsId);

            this.scoreClassName = root.findViewById(R.id.scoreClassName);
            this.val = root.findViewById(R.id.val);
            this.balance = root.findViewById(R.id.balance);

            this.recordTime = root.findViewById(R.id.recordTime);
            this.opTime = root.findViewById(R.id.opTime);
            this.remark = root.findViewById(R.id.remark);
        }
    }
}
