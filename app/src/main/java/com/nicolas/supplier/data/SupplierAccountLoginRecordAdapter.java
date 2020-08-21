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

public class SupplierAccountLoginRecordAdapter extends BaseAdapter {

    private Context mContext;
    private List<SupplierAccountLoginRecord> records;

    public SupplierAccountLoginRecordAdapter(Context context, List<SupplierAccountLoginRecord> records) {
        this.mContext = context;
        this.records = records;
    }

    @Override
    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    @Override
    public Object getItem(int position) {
        return this.records == null ? null : this.records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.login_record_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SupplierAccountLoginRecord record = records.get(position);

        String supplierNameValue = mContext.getString(R.string.supplier) + mContext.getString(R.string.colon) + "<font color=\"black\">" + record.supplierName + "</font>";
        holder.supplierName.setText(Html.fromHtml(supplierNameValue, Html.FROM_HTML_MODE_COMPACT));

        String supplierAccountNameValue = mContext.getString(R.string.supplierAccountName)+ mContext.getString(R.string.colon) +"<font color=\"black\">" + record.supplierAccountName + "</font>";
        holder.supplierAccountName.setText(Html.fromHtml(supplierAccountNameValue, Html.FROM_HTML_MODE_COMPACT));

        String supplierAccountTelValue = mContext.getString(R.string.supplierAccountTel) + mContext.getString(R.string.colon) + "<font color=\"black\">" + record.supplierAccountTel + "</font>";
        holder.supplierAccountTel.setText(Html.fromHtml(supplierAccountTelValue, Html.FROM_HTML_MODE_COMPACT));

        String loginTimeValue = mContext.getString(R.string.loginTime) + mContext.getString(R.string.colon) + "<font color=\"black\">" + record.loginTime + "</font>";
        holder.loginTime.setText(Html.fromHtml(loginTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String ipValue = mContext.getString(R.string.ip) + mContext.getString(R.string.colon) + "<font color=\"black\">" + record.ip + "</font>";
        holder.ip.setText(Html.fromHtml(ipValue, Html.FROM_HTML_MODE_COMPACT));
        
        return convertView;
    }

    private static class ViewHolder {
        private TextView supplierName, supplierAccountName, supplierAccountTel, loginTime, ip;

        private ViewHolder(View root) {
            this.supplierName = root.findViewById(R.id.supplierName);
            this.supplierAccountName = root.findViewById(R.id.supplierAccountName);
            this.supplierAccountTel = root.findViewById(R.id.supplierAccountTel);
            this.loginTime = root.findViewById(R.id.loginTime);
            this.ip = root.findViewById(R.id.ip);
        }
    }
}
