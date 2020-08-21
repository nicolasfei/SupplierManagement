package com.nicolas.supplier.ui.home.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.datetimepickerlibrary.DateTimePickerDialog;
import com.nicolas.multileveltreelibrary.TreeNode;
import com.nicolas.multileveltreelibrary.TreeNodeViewDialog;
import com.nicolas.pullrefreshlibrary.PullRefreshListView;
import com.nicolas.supplier.R;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.component.SingleFloatingDialog;
import com.nicolas.supplier.data.OrderClass;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.supplier.data.OrderInformationAdapter;
import com.nicolas.supplier.data.OrderStatus;
import com.nicolas.supplier.data.PrintStatus;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewOrderActivity extends BaseActivity {
    private static final String TAG = "NewOrderActivity";
    private DrawerLayout drawerLayout;
    private SingleFloatingDialog dialog;
    private CheckBox checkAll;
    private TextView staff;
    private TextView total;
    private Button submit;
    private PullRefreshListView listView;
    private OrderInformationAdapter adapter;

    //以下为查询条件
    private TextView createTime, oldGoodsId, goodsId, goodsClassId, branchId, storeRoomId, roomReceiveTime, inState;
    private RadioButton orderTypeAll, orderTypeFirst, orderTypeCPFR;       //下单类型
    private RadioGroup orderClassChip;
    //    private RadioButton printYet, printNot;                                //是否打印
    private RadioGroup isPrintChip;
    private Button queryClear, query;

    private NewOrderViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        this.viewModel = new ViewModelProvider(this).get(NewOrderViewModel.class);
        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //供应商
        this.staff = findViewById(R.id.staff);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        //排序选择项对话框
        this.dialog = new SingleFloatingDialog(this, 60, this.viewModel.getSortList(), new SingleFloatingDialog.OnSortChoiceListener() {
            @Override
            public void OnSortChoice(int position) {
                resortOfRule(position);
            }
        });
        //listView
        this.listView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new OrderInformationAdapter(this, this.viewModel.getOrderList());
        this.adapter.setOnOrderChangeListener(new OrderInformationAdapter.OnOrderChangeListener() {
            @Override
            public void orderChoice(OrderInformation information) {
                updateOrderCheckStatus();
            }

            @Override
            public void orderGoodsNumChange(String propertyRecordID, int num) {
                viewModel.updateOrderGoodsNum(propertyRecordID, num);
            }

            @Override
            public void orderInStatsChange(String orderID) {
                showManualOrderDialog(orderID);
            }
        });
        this.listView.setAdapter(this.adapter);
        this.listView.setOnLoadingMoreListener(this.loadingMoreListener);
        this.listView.setOnRefreshListener(this.refreshListener);
        //全选
        this.checkAll = findViewById(R.id.all);
        this.checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setOrdersAllChoice(isChecked);
            }
        });
        //合计
        this.total = findViewById(R.id.total);
        this.total.setText(getString(R.string.total));
        //提交
        this.submit = findViewById(R.id.submit);
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先打印订单条码，跳转到订单条码打印页面
                jumpToPrintOrderActivity();
            }
        });
        //setClickable(false)方法一定要放在setOnClickListener()方法之后。不然没有效果
        this.submit.setClickable(false);


        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewOrderActivity.this, OrderPrintActivity.class);
                ArrayList<OrderInformation> list = new ArrayList<>();
                OrderInformation information = new OrderInformation();
                list.add(information);
                intent.putParcelableArrayListExtra("orders", list);
                startActivityForResult(intent, 1);
            }
        });


        /**
         * 查询条件
         */
        this.createTime = findViewById(R.id.createTime);
        this.createTime.setText(viewModel.getQueryCondition().getCreateTime().replace("~", "\u3000~\u3000"));
        this.oldGoodsId = findViewById(R.id.oldGoodsID);
        this.goodsId = findViewById(R.id.goodsID);
        this.goodsClassId = findViewById(R.id.goodsClassId);
        this.branchId = findViewById(R.id.branchId);
        this.storeRoomId = findViewById(R.id.storeRoomId);
        this.roomReceiveTime = findViewById(R.id.roomReceiveTime);
        this.inState = findViewById(R.id.inState);

        this.orderTypeAll = findViewById(R.id.Chip1);
        this.orderTypeFirst = findViewById(R.id.Chip2);
        this.orderTypeCPFR = findViewById(R.id.Chip3);
        switch (viewModel.getQueryCondition().getOrderType()) {
            case OrderClass.ALL:
                this.orderTypeAll.setChecked(true);
                break;
            case OrderClass.FIRST:
                this.orderTypeFirst.setChecked(true);
                break;
            case OrderClass.CPFR:
                this.orderTypeCPFR.setChecked(true);
                break;
        }
        this.orderClassChip = findViewById(R.id.orderClassChip);
        this.orderClassChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Chip1:
                        viewModel.getQueryCondition().setOrderType(OrderClass.ALL);
                        break;
                    case R.id.Chip2:
                        viewModel.getQueryCondition().setOrderType(OrderClass.FIRST);
                        break;
                    case R.id.Chip3:
                        viewModel.getQueryCondition().setOrderType(OrderClass.CPFR);
                        break;
                }
            }
        });

//        this.printYet = findViewById(R.id.c1);
//        this.printNot = findViewById(R.id.c2);
        this.isPrintChip = findViewById(R.id.isPrintChip);
        this.isPrintChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.c1:
                        viewModel.getQueryCondition().setIsPrint(PrintStatus.PRINT);
                        break;
                    case R.id.c2:
                        viewModel.getQueryCondition().setIsPrint(PrintStatus.UN_PRINT);
                        break;
                }
            }
        });

        this.queryClear = findViewById(R.id.clear);
        this.query = findViewById(R.id.yes);

        this.createTime.setOnClickListener(this.onClickListener);
        this.oldGoodsId.setOnClickListener(this.onClickListener);
        this.goodsId.setOnClickListener(this.onClickListener);
        this.goodsClassId.setOnClickListener(this.onClickListener);
        this.branchId.setOnClickListener(this.onClickListener);
        this.inState.setOnClickListener(this.onClickListener);
        this.storeRoomId.setOnClickListener(this.onClickListener);
        this.roomReceiveTime.setOnClickListener(this.onClickListener);
        this.queryClear.setOnClickListener(this.onClickListener);
        this.query.setOnClickListener(this.onClickListener);


        /**
         * 监听是否可以对订单进行多选
         */
        this.viewModel.getOrderMultipleChoiceOperateResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * 监听排序结果
         */
        this.viewModel.getOrderSortResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * 监听订单选中状态改变
         */
        this.viewModel.getOrderChoiceStatusChange().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateOrderCheckStatus();       //更新订单的选中状态
                }
            }
        });

        /**
         * 监听查询结果
         */
        this.viewModel.getOrderQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        Utils.toast(NewOrderActivity.this, (String) msg.obj);
                    }
                }
                if (operateResult.getError() != null) {
                    Utils.toast(NewOrderActivity.this, operateResult.getError().getErrorMsg());
                }
                if (listView.isPushLoadingMore()) {
                    listView.loadMoreFinish();
                }
                if (listView.isPullToRefreshing()) {
                    listView.refreshFinish();
                    //如果订单全选框被勾选，则取消勾选状态
                    if (checkAll.isChecked()) {
                        checkAll.setChecked(false);
                    }
                }
                //更新查询日期
                String queryDate = viewModel.getQueryCondition().getCreateTime();
                if (TextUtils.isEmpty(queryDate)) {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                    queryDate = nowDate + "~" + nowDate;
                }
                listView.updateContentDate(queryDate);
            }
        });

        /**
         * 监听订单数量更新
         */
        this.viewModel.getOrderPropertyUpdateResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Utils.toast(NewOrderActivity.this, R.string.orderNumUpdateSuccess);
                }
                if (operateResult.getError() != null) {
                    Utils.toast(NewOrderActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        /**
         * 数据提交到服务器结果监听
         */
        this.viewModel.getOrderSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(NewOrderActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        //默认查询近3天的订单
        this.queryOrderForCondition();
    }

    /**
     * 用户手动接单--没有打印订单的情况下使用
     */
    private void showManualOrderDialog(final String orderID) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.manual_order)
                .setMessage(R.string.manual_order_ok)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manualSubmitOrderToServer(orderID);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.createTime:       //下单时间
                    DateTimePickerDialog.showDateSlotPickerDialog(NewOrderActivity.this, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                        @Override
                        public void OnDateTimeSlotPick(String start, String end) {
                            updateCreateTime((start + "\u3000~\u3000" + end));
                            viewModel.getQueryCondition().setCreateTime((start + "~" + end));
                        }
                    });
                    break;
                case R.id.goodsID:          //新货号
                    BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsID, InputType.TYPE_CLASS_TEXT, NewOrderActivity.this, new BruceDialog.OnInputFinishListener() {
                        @Override
                        public void onInputFinish(String itemName) {
                            updateGoodsId(itemName);
                            viewModel.getQueryCondition().setGoodsId(itemName);
                        }
                    });
                    break;
                case R.id.oldGoodsID:          //旧货号
                    BruceDialog.showEditInputDialog(R.string.oldGoodsID, R.string.oldGoodsID, InputType.TYPE_CLASS_TEXT, NewOrderActivity.this, new BruceDialog.OnInputFinishListener() {
                        @Override
                        public void onInputFinish(String itemName) {
                            updateOldGoodsId(itemName);
                            viewModel.getQueryCondition().setOldGoodsId(itemName);
                        }
                    });
                    break;
                case R.id.goodsClassId:     //货物类型
                    TreeNodeViewDialog.showTreeNodeViewDialog(NewOrderActivity.this, getString(R.string.goodsClassChoice),
                            SupplierKeeper.getInstance().getGoodsClassTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                                @Override
                                public void onChoice(List<TreeNode> node) {
                                    if (node != null && node.size() > 0) {
                                        updateGoodsClassId((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
                                        for (TreeNode attr : node) {
                                            viewModel.getQueryCondition().addGoodsClassId(attr.id);
                                        }
                                    }
                                }
                            });
                    break;
                case R.id.storeRoomId:       //库房
                    TreeNodeViewDialog.showTreeNodeViewDialog(NewOrderActivity.this, getString(R.string.warehouse_choice),
                            SupplierKeeper.getInstance().getStorehouseTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                                @Override
                                public void onChoice(List<TreeNode> node) {
                                    if (node != null && node.size() > 0) {
                                        updateStoreRoomId((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
                                        for (TreeNode item : node) {
                                            viewModel.getQueryCondition().addStoreRoomID(item.id);
                                        }
                                    }
                                }
                            });
                    break;
                case R.id.branchId:     //分店
                    BruceDialog.showAutoCompleteTextViewDialog(R.string.branch, R.string.branchInput, InputType.TYPE_CLASS_TEXT, NewOrderActivity.this,
                            SupplierKeeper.getInstance().getBranchCodeList(), new BruceDialog.OnInputFinishListener() {
                                @Override
                                public void onInputFinish(String itemName) {
                                    updateBranchId(itemName);
                                    viewModel.getQueryCondition().setBranchID(itemName);
                                }
                            });
                    break;
                case R.id.roomReceiveTime:      //库房接收时间
                    DateTimePickerDialog.showDateSlotPickerDialog(NewOrderActivity.this, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                        @Override
                        public void OnDateTimeSlotPick(String start, String end) {
                            updateRoomReceiveTime((start + "\u3000~\u3000" + end));
                            viewModel.getQueryCondition().setRoomReceiveTime((start + "~" + end));
                        }
                    });
                    break;
                case R.id.inState:          //库房发货状态
                    BruceDialog.showSingleChoiceDialog(R.string.inState, NewOrderActivity.this, OrderStatus.getValues(), new BruceDialog.OnChoiceItemListener() {
                        @Override
                        public void onChoiceItem(String itemName) {
                            updateInState(itemName);
                            viewModel.getQueryCondition().setInState(itemName);
                        }
                    });
                    break;
                case R.id.clear:
                    updateCreateTime("");
                    updateGoodsId("");
                    updateOldGoodsId("");
                    updateStoreRoomId("");
                    updateBranchId("");
                    updateRoomReceiveTime("");
                    updateInState("");
                    updateGoodsClassId("");
                    resetOrderClassChip();
                    resetIsPrintChip();

                    viewModel.clearQueryCondition();
                    break;
                case R.id.yes:
                    queryOrderForCondition();
                    break;
            }
        }
    };

    private void updateCreateTime(String itemValue) {
        createTime.setText(itemValue);
    }

    private void updateGoodsId(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.newGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateOldGoodsId(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.oldGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        oldGoodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateStoreRoomId(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.warehouse) + "\u3000\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" +
                itemValue + "</font>";
        storeRoomId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateBranchId(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.branch) + "\u3000\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        branchId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateRoomReceiveTime(String itemValue) {
        roomReceiveTime.setText(itemValue);
    }

    private void updateInState(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.shipment_status) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        inState.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsClassId(String itemValue) {
        String value = NewOrderActivity.this.getString(R.string.goodsClass) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" +
                itemValue + "</font>";
        goodsClassId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void resetOrderClassChip() {
        orderClassChip.clearCheck();
    }

    private void resetIsPrintChip() {
        isPrintChip.clearCheck();
    }


    /**
     * 查询订单
     */
    private void queryOrderForCondition() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        //在查询条件发生改变时，才查询
        if (viewModel.getQueryCondition().isQueryConditionUpdate()) {
            BruceDialog.showProgressDialog(this, getString(R.string.querying));
            viewModel.queryOrder();
            viewModel.getQueryCondition().resetQueryConditionUpdateStatus();    //查询条件是否发生改变置为false
        }
    }

    /**
     * 跳转到订单条码打印页面
     */
    private void jumpToPrintOrderActivity() {
        Intent intent = new Intent(NewOrderActivity.this, OrderPrintActivity.class);
        intent.putParcelableArrayListExtra("orders", viewModel.getCheckedOrders());
        for (OrderInformation order : viewModel.getCheckedOrders()) {
            Log.d(TAG, "jumpToPrintOrderActivity: " + order.goodsId + "---" + order.orderType.toString());
        }
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {     //打印成功
                submitOrderToServer();
            } else if (resultCode == -1) {
                Utils.toast(this, R.string.print_default_check);
            }
        }
    }

    /**
     * 订单提交
     */
    private void submitOrderToServer() {
        BruceDialog.showProgressDialog(this, getString(R.string.submitting));
        viewModel.submitOrders();       //提交订单更新数据到服务器
    }

    /**
     * 手动提交到服务器
     *
     * @param orderID 订单ID
     */
    private void manualSubmitOrderToServer(String orderID) {
        BruceDialog.showProgressDialog(this, getString(R.string.submitting));
        viewModel.manualSubmitOrders(orderID);       //提交订单更新数据到服务器
    }

    /**
     * 订单被选中/被取消后更新界面
     */
    private void updateOrderCheckStatus() {
        this.adapter.notifyDataSetChanged();

        String oderTotal;
        if (this.viewModel.getTotalNum() > 0) {
            this.submit.setClickable(true);
            this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_red));
            oderTotal = getString(R.string.total) + getString(R.string.colon) +
                    getString(R.string.ordernum) + getString(R.string.colon) + "<font color=\"green\">" + "x" + this.viewModel.getTotalNum() + "</font>" +
                    "\u3000\u3000\u3000\u3000" +
                    getString(R.string.price) + getString(R.string.colon) + "<font color=\"red\">" + getString(R.string.money) + this.viewModel.getTotalPrice() + "</font>";
        } else {
            this.submit.setClickable(false);
            this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
            oderTotal = getString(R.string.total);
        }
        this.total.setText(Html.fromHtml(oderTotal, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 更新供应商
     *
     * @param staff 供应商
     */
    private void updateStaff(String staff) {
        String value = getString(R.string.staff) + "\u3000\u3000\u3000" + "<font color=\"black\">" + staff + "</font>";
        this.staff.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 重新排序
     *
     * @param position position
     */
    private void resortOfRule(int position) {
        BruceDialog.showProgressDialog(this, "");
        viewModel.resortOfRule(position);
    }

    /**
     * 订单加载更多
     */
    private PullRefreshListView.OnLoadingMoreListener loadingMoreListener = new PullRefreshListView.OnLoadingMoreListener() {
        @Override
        public void onLoadingMore() {
            viewModel.loadMoreOrders();
        }
    };

    /**
     * 订单刷新
     */
    private PullRefreshListView.OnRefreshListener refreshListener = new PullRefreshListView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            viewModel.refreshOrders();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_order_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.app_bar_search));
        searchView.setQueryHint(getString(R.string.search_hint));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_screen:
                this.drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.menu_sort:
                showSortDialog();
                break;
            case R.id.menu_more:
                showOrderMultipleChoiceBox();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 显示全部选择box
     */
    private void showOrderMultipleChoiceBox() {
        if (this.checkAll.getVisibility() == View.VISIBLE) {
            this.checkAll.setVisibility(View.INVISIBLE);
            this.checkAll.setChecked(false);
            this.viewModel.setOrderMultipleChoice(false);
        } else {
            this.checkAll.setVisibility(View.VISIBLE);
            this.viewModel.setOrderMultipleChoice(true);
        }
    }

    /**
     * 显示排序对话框
     */
    private void showSortDialog() {
        this.dialog.show();
    }
}
