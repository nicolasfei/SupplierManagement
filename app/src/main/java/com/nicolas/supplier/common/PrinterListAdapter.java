package com.nicolas.supplier.common;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.nicolas.printerlibraryforufovo.PrinterDevice;
import com.nicolas.printerlibraryforufovo.PrinterDeviceGroup;
import com.nicolas.supplier.R;

import java.util.List;

import static com.nicolas.printerlibraryforufovo.PrinterDevice.LINK_TYPE_BLUETOOTH;
import static com.nicolas.printerlibraryforufovo.PrinterDevice.LINK_TYPE_WIFI;


public class PrinterListAdapter extends GroupedRecyclerViewAdapter {

    private List<PrinterDeviceGroup> group;
    private Context mContext;
    private OnPrinterOperationListener listener;

    public PrinterListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setPrinterDeviceGroup(List<PrinterDeviceGroup> group) {
        this.group = group;
    }

    @Override
    public int getGroupCount() {
        return group == null ? 0 : group.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return group.get(groupPosition).getChildCount();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.printer_list_head;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.printer_list_item;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, final int groupPosition) {
        PrinterDeviceGroup groupItem = group.get(groupPosition);
        switch (groupItem.getGroupType()) {
            case LINK_TYPE_BLUETOOTH:
                holder.setText(R.id.title, mContext.getString(R.string.printer_bluetooth_title));

                //设置是否显示正在扫描中
                holder.setVisible(R.id.progressBar, groupItem.isScanning());
                holder.setVisible(R.id.scanning, groupItem.isScanning());

                if (groupItem.isPortOpen()) {
                    holder.setText(R.id.describe, mContext.getString(R.string.printer_bluetooth_describe_on));
                    holder.setTextColor(R.id.describe, mContext.getColor(R.color.enable_gray));
                } else {
                    holder.setText(R.id.describe, mContext.getString(R.string.printer_bluetooth_describe_off));
                    holder.setTextColor(R.id.describe, mContext.getColor(R.color.disable_gray));
                }
                Switch blueSwitch = holder.get(R.id.switch1);
                blueSwitch.setChecked(groupItem.isPortOpen());
                blueSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (listener != null) {
                            listener.printerPortChange(groupPosition, isChecked);
                        }
                    }
                });
                break;
            case LINK_TYPE_WIFI:
                holder.setText(R.id.title, mContext.getString(R.string.printer_wifi_title));

                //设置是否显示正在扫描中
                holder.setVisible(R.id.progressBar, groupItem.isScanning());
                holder.setVisible(R.id.scanning, groupItem.isScanning());

                holder.setText(R.id.describe, mContext.getString(R.string.printer_wifi_describe));
                holder.setVisible(R.id.describe, groupItem.isPortOpen());
                Switch wifiSwitch = holder.get(R.id.switch1);
                wifiSwitch.setChecked(groupItem.isPortOpen());
                wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (listener != null) {
                            listener.printerPortChange(groupPosition, isChecked);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, final int groupPosition, final int childPosition) {
        PrinterDeviceGroup groupItem = group.get(groupPosition);
        PrinterDevice device = groupItem.getChild(childPosition);

//        TextView icon = holder.get(R.id.icon);                              //设备图标
//        icon.setCompoundDrawables(mContext.getDrawable(R.drawable.ic_print_black_48dp), null, null, null);
        holder.setText(R.id.textView8, device.getDeviceName() + ((device.getAlias() == null) ? "" : ("-" + device.getAlias())));     //设备名

        holder.setVisible(R.id.progressBar2, device.isLinking());
        holder.setText(R.id.textView9, device.isLinking() ? mContext.getString(R.string.printer_linking) :
                (device.isLink() ? mContext.getString(R.string.printer_linked) : null));
        holder.setVisible(R.id.textView9, device.isLink() || device.isLinking());   //设备是否正在连接中,或者已经连接上

        TextView operation = holder.get(R.id.textView10);
        operation.setClickable(true);
        operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.printerChecked(groupPosition, childPosition);
                }
            }
        });
    }

    public void setOnPrinterOperationListener(OnPrinterOperationListener listener) {
        this.listener = listener;
    }

    public interface OnPrinterOperationListener {
        void printerPortChange(int groupPosition, boolean status);

        void printerChecked(int groupPosition, int childPosition);
    }
}
