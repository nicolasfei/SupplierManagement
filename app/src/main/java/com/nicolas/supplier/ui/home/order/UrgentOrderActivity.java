package com.nicolas.supplier.ui.home.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.multileveltree.TreeNodeViewDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshMenuItem;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshSwipeMenuListView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.component.SingleFloatingDialog;
import com.nicolas.supplier.data.OrderDataHolder;
import com.nicolas.supplier.data.OrderGoodsCountAdapter;
import com.nicolas.supplier.data.OrderInformation;
import com.nicolas.supplier.data.OrderInformationAdapter;
import com.nicolas.supplier.data.OrderQueryCondition;
import com.nicolas.supplier.data.OrderStatistics;
import com.nicolas.supplier.data.OrderStatus;
import com.nicolas.supplier.data.OrderValid;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.nicolas.supplier.data.OrderStatus.SWAITED_ID;
import static com.nicolas.supplier.data.OrderStatus.SWAIT_ID;

public class UrgentOrderActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private SingleFloatingDialog dialog;
    private CheckBox checkAll;
    private TextView staff;     //供应商
    private TextView detailed;  //明细
    private TextView total;     //合计
    private Button submit;      //批量提交
    private Button batchVoid;   //批量作废
    private PullRefreshSwipeMenuListView listView;
    private OrderInformationAdapter adapter;
    private boolean isOrderGoodsCount = false;        //订单货号统计
    private boolean isOrderPrint = false;             //打印所有订单

    //以下为查询条件
    private TextView createTime, oldGoodsId, goodsId, goodsClassId, branchId, storeRoomId, orderID, printTime;
    private RadioGroup inStateChip;         //订单接收状态--供应商待接单，供应商已接单，库房已收货，库房已发货，分店已收货
    private RadioGroup isValidChip;         //订单状态--正常,作废

    private boolean isInStateChipClear = false;      //标记清理--订单状态
    private boolean isValidChipClear = false;        //标记清理isValidChip

    private boolean manualCheckAll = false;     //手动设置checkAll状态
    private UrgentOrderViewModel viewModel;
    private boolean isManualSubmit = false;     //手动单个接单

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent);

        this.viewModel = new ViewModelProvider(this).get(UrgentOrderViewModel.class);
        this.drawerLayout = findViewById(R.id.drawer_layout);
        //添加打印机实时状态监听
        PrinterManager.getInstance().setOnPrinterStatusReaderCallBack(this.printerRealCheckCallBack);

        //供应商
        this.staff = findViewById(R.id.staff);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        //明细
        this.detailed = findViewById(R.id.detailed);

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
            public void orderGoodsNumChange(String orderID, String propertyRecordID, int num) {
                showProgressDialog(getString(R.string.sendAmountUpdate));
                viewModel.updateOrderGoodsNum(orderID, propertyRecordID, num);
            }

            @Override
            public void orderTakeAndInValidOperation(String orderID) {
                showManualOrderDialog(orderID);
            }

            @Override
            public void orderInValidOperation(String orderID) {
                showOrderInValidDialog(orderID);
            }

            @Override
            public void orderPropertyQuery(String orderID) {
                queryOrderProperty(orderID);
            }
        });
        this.listView.setAdapter(this.adapter);
        this.listView.setOnLoadingMoreListener(this.loadingMoreListener);
        this.listView.setOnRefreshListener(this.refreshListener);
        List<PullRefreshMenuItem> items = new ArrayList<>();
        // create "invalid" item
        PullRefreshMenuItem invalid = new PullRefreshMenuItem(this);
        // set item background
        invalid.setBackground(new ColorDrawable(Color.GRAY));
        // set item width
        invalid.setWidth(Tool.dp2px(this, 90));
        // set item title
        invalid.setTitle(getString(R.string.inValid));
        // set item title fontsize
        invalid.setTitleSize(18);
        // set item title font color
        invalid.setTitleColor(Color.BLACK);
        // add to menu
        items.add(invalid);

        // create "Receiving" item
        PullRefreshMenuItem Receiving = new PullRefreshMenuItem(this);
        // set item background
        Receiving.setBackground(new ColorDrawable(Color.GREEN));
        // set item width
        Receiving.setWidth(Tool.dp2px(this, 90));
        // set item title
        Receiving.setTitle(getString(R.string.receiving));
        // set item title fontsize
        Receiving.setTitleSize(18);
        // set item title font color
        Receiving.setTitleColor(Color.BLACK);
        items.add(Receiving);
        this.listView.setPullRefreshSwipeMenu(items);
        this.listView.setOnPullRefreshMenuItemClickListener(new PullRefreshSwipeMenuListView.OnPullRefreshMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, int index) {
                OrderInformation order = viewModel.getOrderList().get(position);
                int status = order.inState.getStatusID();
                switch (index) {
                    case 0:
                        //订单作废
                        if (status == SWAIT_ID || status == SWAITED_ID) {
                            showOrderInValidDialog(order.id);
                        } else {
                            BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                                    order.valid.equals(OrderValid.INVALID) ? getString(R.string.order_has_invlid) :
                                            getString(R.string.order_cannot_invlid));
                        }
                        break;
                    case 1:
                        //手动接单
                        if (status == SWAIT_ID) {
                            showManualSubmitOrderToServerDialog(order.id);
                        } else {
                            BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                                    order.valid.equals(OrderValid.INVALID) ? getString(R.string.order_has_invlid) :
                                            getString(R.string.order_has_take));
                        }
                        break;
                }
                return true;
            }
        });

        //全选
        this.checkAll = findViewById(R.id.all);
        this.checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (manualCheckAll) {
                    manualCheckAll = false;
                    return;
                }
                viewModel.setOrderAllSelectStatus(isChecked);
            }
        });

        //合计
        this.total = findViewById(R.id.total);
        this.total.setText(getString(R.string.total));
        //提交
        this.submit = findClickView(R.id.submit);
        //setClickable(false)方法一定要放在setOnClickListener()方法之后。不然没有效果
        this.submit.setEnabled(false);
        //批量作废
        this.batchVoid = findClickView(R.id.batchVoid);
        this.batchVoid.setEnabled(false);

        //查询条件
        OrderQueryCondition condition = viewModel.getQueryCondition();
        this.createTime = findClickView(R.id.createTime);
        this.updateCreateTime(condition.getCreateTime().replace("~", "\u3000~\u3000"));

        this.oldGoodsId = findClickView(R.id.oldGoodsID);
        this.updateOldGoodsId(condition.getOldGoodsId());

        this.goodsId = findClickView(R.id.goodsID);
        this.updateGoodsId(condition.getGoodsId());

        this.goodsClassId = findClickView(R.id.goodsClassId);
        this.updateGoodsClassId(condition.getGoodsClassId());

        this.branchId = findClickView(R.id.branchId);
        this.updateBranchId(condition.getBranchID());

        this.orderID = findClickView(R.id.orderID);
        this.updateOrderID(condition.getOrderID());

        this.storeRoomId = findClickView(R.id.storeRoomId);
        this.updateStoreRoomId(condition.getStoreRoomID());

        //订单发货状态
        this.inStateChip = findViewById(R.id.inStateChip);
        this.inStateChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isInStateChipClear) {
                    isInStateChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.inStateChip0:
                        viewModel.getQueryCondition().setInState(OrderStatus.SWAIT);
                        break;
                    case R.id.inStateChip1:
                        viewModel.getQueryCondition().setInState(OrderStatus.SWAITED);
                        break;
                    case R.id.inStateChip2:
                        viewModel.getQueryCondition().setInState(OrderStatus.ROOM_RECEIVE);
                        break;
                    case R.id.inStateChip3:
                        viewModel.getQueryCondition().setInState(OrderStatus.ROOM_SEND);
                        break;
                    case R.id.inStateChip4:
                        viewModel.getQueryCondition().setInState(OrderStatus.BRANCH_RECEIVE);
                        break;
                    default:
                        break;
                }
            }
        });
        this.initInStateChipSelect(condition.getInStateShow());

        //订单状态--正常，作废
        this.isValidChip = findViewById(R.id.isValidChip);
        this.isValidChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isValidChipClear) {
                    isValidChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.isValid1:
                        viewModel.getQueryCondition().setValid(getString(R.string.normal));
                        break;
                    case R.id.isValid2:
                        viewModel.getQueryCondition().setValid(getString(R.string.inValid));
                        break;
                }
            }
        });
        this.initIsValidChipSelect(condition.getValid());

        //订单打印时间
        this.printTime = findClickView(R.id.printTime);

        //查询条件clear
        findClickView(R.id.oldGoodsIDClear);
        findClickView(R.id.goodsIDClear);
        findClickView(R.id.goodsClassIdClear);
        findClickView(R.id.branchIdClear);
        findClickView(R.id.storeRoomIdClear);
        findClickView(R.id.orderIDClear);
        findClickView(R.id.inStateClear);
        findClickView(R.id.isValidClear);
        findClickView(R.id.createTimeClear);
        findClickView(R.id.printTimeClear);
        findClickView(R.id.clear);
        findClickView(R.id.yes);

        //监听是否可以对订单选择结果
        this.viewModel.getOrderCanSelectResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //监听排序结果
        this.viewModel.getOrderSortResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //监听订单选中状态改变
        this.viewModel.getOrderSelectUpdateResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    updateOrderCheckStatus();       //更新订单的选中状态
                }
            }
        });

        //监听订单作废
        this.viewModel.getOrderInValidResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            getString(R.string.order_invalid_success));
                }
                if (operateResult.getError() != null) {
//                    Utils.toast(UrgentOrderActivity.this, operateResult.getError().getErrorMsg());
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //监听查询结果
        this.viewModel.getOrderQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null && msg.what == 1) {     //无订单数据
                        adapter.notifyDataSetChanged();
                        updateListViewStatus();
                        updateDetailed();                   //更新订单明细
                        updateOrderCheckStatus();
                        dismissProgressDialog();
                        BruceDialog.showPromptDialog(UrgentOrderActivity.this, (String) msg.obj);
                    } else {
                        //订单查询成功，查询订单配送顺序
                        viewModel.queryOrderDistribution();
                    }
                }
                if (operateResult.getError() != null) {
                    adapter.notifyDataSetChanged();
                    updateListViewStatus();
                    dismissProgressDialog();
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //配送顺序表查询结果--订单查询完成
        this.viewModel.getOrderDistributionQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                updateListViewStatus();
                adapter.notifyDataSetChanged();
                if (operateResult.getSuccess() != null) {
                    updateDetailed();           //更新订单明细
                    updateOrderCheckStatus();   //更新订单选择状态
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                                (String) msg.obj);
                    }
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //监听订单属性查询结果
        this.viewModel.getOrderPropertyQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                                (String) msg.obj);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
//                    Utils.toast(UrgentOrderActivity.this, operateResult.getError().getErrorMsg());
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //监听所有订单属性批量下载结果
        this.viewModel.getOrderPropertyAllDownResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    //跳转到订单打印页面
                    if (isOrderPrint) {
                        isOrderPrint = false;
                        printOrderFormSelect();
                    }
                    //显示订单货号统计
                    if (isOrderGoodsCount) {
                        isOrderGoodsCount = false;
//                        showOrderStatisticDialog();
                    }
                }
                if (operateResult.getError() != null) {
//                    Utils.toast(UrgentOrderActivity.this, operateResult.getError().getErrorMsg());
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //监听订单数量更新
        this.viewModel.getOrderPropertyUpdateResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            getString(R.string.orderNumUpdateSuccess));
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //数据提交到服务器结果监听
        this.viewModel.getOrderSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    //更新订单状态
                    adapter.notifyDataSetChanged();
//                    //更新订单数量合计，及提交按钮状态
//                    updateOrderCheckStatus();
                    //提交成功，打印订单，跳转到订单打印页面
                    if (isManualSubmit) {        //手动提交订单不跳转到打印页面
                        isManualSubmit = false;
                        return;
                    }
                    printOrderFormSelect();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this,
                            operateResult.getError().getErrorMsg());
                }
            }
        });

        //默认查询近3天的订单
        showProgressDialog(getString(R.string.querying));
        viewModel.queryOrder();
    }

    /**
     * 用户手动接单--没有打印订单的情况下使用
     */
    private void showManualOrderDialog(final String orderID) {
        String[] operation = new String[]{getString(R.string.manual_order), getString(R.string.OrderInValid)};
        BruceDialog.showSingleChoiceDialog(R.string.orderOperation, this, operation, new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (itemName.equals(getString(R.string.manual_order))) {
                    showManualSubmitOrderToServerDialog(orderID);
                }
                if (itemName.equals(getString(R.string.OrderInValid))) {
                    showOrderInValidDialog(orderID);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createTime:       //下单时间
                OrderQueryCondition condition = viewModel.getQueryCondition();
                String createTime = condition.getCreateTime();
                String start = "";
                String end = "";
                if (!TextUtils.isEmpty(createTime)) {
                    String[] times = createTime.split("~");
                    if (times.length >= 2) {
                        start = times[0];
                        end = times[1];
                    }
                }
                DateTimePickerDialog.showDateSlotPickerDialog(UrgentOrderActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        updateCreateTime((start + "\u3000~\u3000" + end));
                        viewModel.getQueryCondition().setCreateTime((start + "~" + end));
                    }
                });
                break;
            case R.id.goodsID:          //新货号
                BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsID, InputType.TYPE_CLASS_TEXT, UrgentOrderActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        updateGoodsId(itemName);
                        viewModel.getQueryCondition().setGoodsId(itemName);
                    }
                });
                break;
            case R.id.oldGoodsID:          //旧货号
                BruceDialog.showEditInputDialog(R.string.oldGoodsID, R.string.oldGoodsID, InputType.TYPE_CLASS_TEXT, UrgentOrderActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        updateOldGoodsId(itemName);
                        viewModel.getQueryCondition().setOldGoodsId(itemName);
                    }
                });
                break;
            case R.id.goodsClassId:     //货物类型
                TreeNodeViewDialog.showTreeNodeViewDialog(UrgentOrderActivity.this, getString(R.string.goodsClassChoice),
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
                TreeNodeViewDialog.showTreeNodeViewDialog(UrgentOrderActivity.this, getString(R.string.warehouse_choice),
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
            case R.id.orderID:      //订单号
                BruceDialog.showEditInputDialog(R.string.orderID, R.string.orderID, InputType.TYPE_CLASS_TEXT, UrgentOrderActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        updateOrderID(itemName);
                        viewModel.getQueryCondition().setOrderID(itemName);
                    }
                });
                break;
            case R.id.branchId:     //分店
                BruceDialog.showAutoCompleteTextViewDialog(R.string.branch, R.string.branchInput, InputType.TYPE_CLASS_TEXT, UrgentOrderActivity.this,
                        SupplierKeeper.getInstance().getBranchCodeList(), new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                updateBranchId(itemName);
                                viewModel.getQueryCondition().setBranchID(itemName);
                            }
                        });
                break;
            case R.id.printTime:
                DateTimePickerDialog.showDateTimePickerDialog(UrgentOrderActivity.this, viewModel.getQueryCondition().getPrintTime(),
                        new DateTimePickerDialog.OnDateTimePickListener() {
                            @Override
                            public void OnDateTimePick(String dateTime) {
                                viewModel.getQueryCondition().setPrintTime(dateTime);
                                updatePrintTime(dateTime);
                            }
                        });
                break;
            case R.id.clear:
                viewModel.clearQueryCondition();
                updateCreateTime(viewModel.getQueryCondition().getCreateTime().replace("~", "\u3000~\u3000"));
                updateGoodsId("");
                updateOldGoodsId("");
                updateStoreRoomId("");
                updateBranchId("");
                updateGoodsClassId("");
                updateOrderID("");
                updatePrintTime("");
                resetValidChip();
                break;
            case R.id.yes:
                queryOrderForCondition();
                break;
            case R.id.submit:
                //先批量加载订单属性，然后打印订单条码---跳转到订单条码打印页面
//                this.isOrderPrint = true;
//                showProgressDialog(UrgentOrderActivity.this, getString(R.string.orderQueryProperties));
//                viewModel.batchDownloadOrderProperty(true);
                submitDataAndPrint();
                break;
            case R.id.batchVoid:
                //批量作废
                showBatchOrderInValidDialog();
                break;
            case R.id.loadAll:
                showProgressDialog(getString(R.string.loadingAllOrder));
                viewModel.queryAllOrder();      //查询所有订单
                break;
            //查询条件清空
            case R.id.oldGoodsIDClear:
                viewModel.getQueryCondition().setOldGoodsId("");
                updateOldGoodsId("");
                break;
            case R.id.goodsIDClear:
                viewModel.getQueryCondition().setGoodsId("");
                updateGoodsId("");
                break;
            case R.id.goodsClassIdClear:
                viewModel.getQueryCondition().addGoodsClassId("");
                updateGoodsClassId("");
                break;
            case R.id.branchIdClear:
                viewModel.getQueryCondition().setBranchID("");
                updateBranchId("");
                break;
            case R.id.storeRoomIdClear:
                viewModel.getQueryCondition().addStoreRoomID("");
                updateStoreRoomId("");
                //清空库房树选择的状态
                SupplierKeeper.getInstance().clearStorehouseSelect();
                break;
            case R.id.inStateClear:
                viewModel.getQueryCondition().setInState(OrderStatus.NONE);
                resetInStateChip();
                break;
            case R.id.orderIDClear:
                viewModel.getQueryCondition().setOrderID("");
                updateOrderID("");
                break;
            case R.id.isValidClear:
                viewModel.getQueryCondition().setValid("");
                resetValidChip();
                break;
            case R.id.createTimeClear:
                viewModel.getQueryCondition().setCreateTime(Tool.getNearlyOneDaysDateSlot());
                updateCreateTime(viewModel.getQueryCondition().getCreateTime().replace("~", "\u3000~\u3000"));
                break;
            case R.id.printTimeClear:
                viewModel.getQueryCondition().setPrintTime("");
                updatePrintTime("");
            default:
                break;
        }
    }

    /**
     * 提交数据并打印
     */
    private void submitDataAndPrint() {
        BruceDialog.showAlertDialog(this, getString(R.string.submit), getString(R.string.submit_print), new BruceDialog.OnAlertDialogListener() {
            @Override
            public void onSelect(boolean confirm) {
                if (confirm) {
                    //提交数据
                    submitOrderToServer();
                }
            }
        });
    }

    /**
     * 订单查询后更新listView status
     */
    private void updateListViewStatus() {
        if (listView.isPushLoadingMore()) {
            listView.loadMoreFinish();
        }
        if (listView.isPullToRefreshing()) {
            listView.refreshFinish();
        }
        //更新查询日期
        listView.updateContentDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
    }

    private void updateCreateTime(String itemValue) {
        createTime.setText(itemValue);
    }

    private void updatePrintTime(String itemValue) {
        printTime.setText(itemValue);
    }

    private void updateGoodsId(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.newGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateOrderID(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.orderID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        orderID.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateOldGoodsId(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.oldGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        oldGoodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateStoreRoomId(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.warehouse) + "\u3000\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" +
                itemValue + "</font>";
        storeRoomId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateBranchId(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.branch) + "\u3000\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        branchId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsClassId(String itemValue) {
        String value = UrgentOrderActivity.this.getString(R.string.goodsClass) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" +
                itemValue + "</font>";
        goodsClassId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void initInStateChipSelect(String itemValue) {
        switch (itemValue) {
            case OrderStatus.NONE:
                isInStateChipClear = true;
                this.inStateChip.clearCheck();
                break;
            case OrderStatus.SWAIT:
                isInStateChipClear = true;
                this.inStateChip.check(R.id.inStateChip0);
                break;
            case OrderStatus.SWAITED:
                isInStateChipClear = true;
                this.inStateChip.check(R.id.inStateChip1);
                break;
            case OrderStatus.ROOM_RECEIVE:
                isInStateChipClear = true;
                this.inStateChip.check(R.id.inStateChip2);
                break;
            case OrderStatus.ROOM_SEND:
                isInStateChipClear = true;
                this.inStateChip.check(R.id.inStateChip3);
                break;
            case OrderStatus.BRANCH_RECEIVE:
                isInStateChipClear = true;
                this.inStateChip.check(R.id.inStateChip4);
                break;
            default:
                break;
        }
    }

    private void initIsValidChipSelect(String itemValue) {
        switch (itemValue) {
            case OrderValid.NONE:
                isValidChipClear = true;
                this.isValidChip.clearCheck();
                break;
            case OrderValid.NORMAL:
                isValidChipClear = true;
                this.isValidChip.check(R.id.isValid1);
                break;
            case OrderValid.INVALID:
                isValidChipClear = true;
                this.isValidChip.check(R.id.isValid2);
                break;
            default:
                break;
        }
    }

    private void resetValidChip() {
        isValidChipClear = true;
        isValidChip.clearCheck();
    }

    private void resetInStateChip() {
        isInStateChipClear = true;
        inStateChip.clearCheck();
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
            showProgressDialog(getString(R.string.querying));
            viewModel.queryOrder();
            viewModel.getQueryCondition().resetQueryConditionUpdateStatus();    //查询条件是否发生改变置为false
        }
    }

    /**
     * 查询订单属性
     *
     * @param orderID 订单ID
     */
    private void queryOrderProperty(String orderID) {
        showProgressDialog(getString(R.string.querying));
        viewModel.queryOrderProperty(orderID);
    }

    /**
     * 通过选择订单的方式打印
     */
    private void printOrderFormSelect() {
        Intent intent = new Intent(UrgentOrderActivity.this, OrderPrintActivity.class);
        OrderDataHolder.getInstance().setData("orders", viewModel.getCheckedOrders());
        OrderDataHolder.getInstance().setData("distributions", viewModel.getOrderDistributionList());
        startActivityForResult(intent, 1);
    }

    /**
     * 通过统计数据的方式打印
     */
    private void printOrderFormStatistics(String goodsID) {
        Intent intent = new Intent(UrgentOrderActivity.this, OrderPrintActivity.class);
        OrderDataHolder.getInstance().setData("orders", viewModel.getOrdersFormGoodsID(goodsID));
        OrderDataHolder.getInstance().setData("distributions", viewModel.getOrderDistributionList());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == 1) {     //打印成功
//                submitOrderToServer();
//                if (this.checkAll.isChecked()) {            //取消checkAll状态
//                    manualCheckAll = true;
//                    this.checkAll.setChecked(false);
//                }
//            } else if (resultCode == -1) {
//                BruceDialog.showAlertDialog(UrgentOrderActivity.this, getString(R.string.failed),
//                        getString(R.string.print_default_check), new BruceDialog.OnAlertDialogListener() {
//                            @Override
//                            public void onSelect(boolean confirm) {
//
//                            }
//                        });
//            }
//        }
    }

    /**
     * 订单提交
     */
    private void submitOrderToServer() {
        showProgressDialog(getString(R.string.submitting));
        //先检查打印机状态
        PrinterManager.getInstance().isLinkedPrinter();
    }

    /**
     * 打印机实时状态监听
     */
    private PrinterManager.OnPrinterStatusReaderCallBack printerRealCheckCallBack = new PrinterManager.OnPrinterStatusReaderCallBack() {
        @Override
        public void printerRealStatus(int status, int mode, String msg) {
            switch (status) {
                case 0:         //打印机正常连接
                    //TSC模式
                    if (mode == 2) {
                        //提交数据
                        viewModel.submitOrders();       //提交订单更新数据到服务器
                    } else {
                        dismissProgressDialog();
                        BruceDialog.showPromptDialog(UrgentOrderActivity.this, "打印机模式非标签打印模式，请检查打印机!");
                    }
                    break;
                default:
                    dismissProgressDialog();
                    BruceDialog.showPromptDialog(UrgentOrderActivity.this, msg);
                    break;
            }
        }
    };

    /**
     * 手动提交到服务器dialog
     *
     * @param orderID 订单ID
     */
    private void showManualSubmitOrderToServerDialog(final String orderID) {
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

    /**
     * 手动提交到服务器
     *
     * @param orderID 订单ID
     */
    private void manualSubmitOrderToServer(String orderID) {
        showProgressDialog(getString(R.string.submitting));
        this.isManualSubmit = true;
        viewModel.manualSubmitOrders(orderID);       //提交订单更新数据到服务器
    }

    /**
     * 订单作废确认dialog
     *
     * @param orderID orderID
     */
    private void showOrderInValidDialog(final String orderID) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.inValid_order)
                .setMessage(R.string.inValid_order_ok)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderInValid(orderID);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * 订单作废
     *
     * @param orderID 订单作废
     */
    private void orderInValid(String orderID) {
        showProgressDialog(getString(R.string.submitting));
        viewModel.orderInValid(orderID);       //提交订单更新数据到服务器
    }

    /**
     * 订单作废确认dialog
     */
    private void showBatchOrderInValidDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.inValid_order)
                .setMessage(R.string.is_inValid_select_order)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderBatchValid();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * 批量作废订单
     */
    private void orderBatchValid() {
        showProgressDialog(getString(R.string.orderBatchInValid));
        viewModel.orderBatchInValid();
    }

    /**
     * 订单被选中/被取消后更新界面
     */
    private void updateOrderCheckStatus() {
        String oderTotal;
        if (this.viewModel.getTotalNum() > 0) {
//            this.submit.setClickable(true);
//            this.batchVoid.setClickable(true);
            this.submit.setEnabled(true);
            this.batchVoid.setEnabled(true);
            this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_red));
            oderTotal = getString(R.string.total) + "\u3000" +
                    getString(R.string.ordernum) + getString(R.string.colon) + "<font color=\"blue\">" + "x" + this.viewModel.getTotalNum() + "</font>";
//                    +
//                    "\u3000\u3000" +
//                    getString(R.string.price) + getString(R.string.colon) + "<font color=\"red\">" + getString(R.string.money) + this.viewModel.getTotalPrice() + "</font>";
        } else {
//            this.submit.setClickable(false);
//            this.batchVoid.setClickable(false);
            this.submit.setEnabled(false);
            this.batchVoid.setEnabled(false);
            this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
            oderTotal = getString(R.string.total);
        }
        this.total.setText(Html.fromHtml(oderTotal, Html.FROM_HTML_MODE_COMPACT));

        //更新checkAll状态
        if (this.viewModel.getIsAllOrderChecked()) {
            if (!checkAll.isChecked()) {
                manualCheckAll = true;
                checkAll.setChecked(true);
                viewModel.updateOrderAllSelectStatus(true);
            }
        } else {
            if (checkAll.isChecked()) {
                manualCheckAll = true;
                checkAll.setChecked(false);
                viewModel.updateOrderAllSelectStatus(false);
            }
        }
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
     * 更新明细
     */
    private void updateDetailed() {
        String value = "\u3000\u3000" + getString(R.string.orderTotal) + getString(R.string.colon) + "<font color=\"black\">" + this.viewModel.getOrderTotal() + "</font>" +
                "\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000\u3000" +
                getString(R.string.query_date) + getString(R.string.colon) + "<font color=\"black\">" + viewModel.getQueryCondition().getCreateTime() + "</font>";
        this.detailed.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 重新排序
     *
     * @param position position
     */
    private void resortOfRule(int position) {
        showProgressDialog("");
//        viewModel.resortOfRule(position);
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
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.app_bar_search));
//        searchView.setQueryHint(getString(R.string.search_hint));
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
            case R.id.menu_count:
                goodsCodeCount();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 订单货号统计
     */
    private void goodsCodeCount() {
//        showOrderStatisticDialog();
    }

    @Override
    protected void onDestroy() {
        //清空库房树选择的状态
        SupplierKeeper.getInstance().clearStorehouseSelect();
        super.onDestroy();
    }

    /**
     * 显示全部选择box
     */
    private void showOrderMultipleChoiceBox() {
        if (this.checkAll.getVisibility() == View.VISIBLE) {
            this.checkAll.setChecked(false);
            this.checkAll.setVisibility(View.INVISIBLE);
            this.viewModel.setOrderCanSelect(false);
        } else {
            this.checkAll.setVisibility(View.VISIBLE);
            this.viewModel.setOrderCanSelect(true);
        }
    }

    /**
     * 显示排序对话框
     */
    private void showSortDialog() {
        this.dialog.show();
    }

    /**
     * 显示订单货号统计对话框
     */
    AlertDialog printOrderFormStatisticsDialog;
    ListView count;
    OrderGoodsCountAdapter countAdapter;

//    private void showOrderStatisticDialog() {
//        List<OrderStatistics> data = viewModel.getOrderStatisticList();
//        if (data.size() == 0) {
//            BruceDialog.showPromptDialog(UrgentOrderActivity.this,
//                    getString(R.string.noOrder));
////            this.orderGoodsCounting = false;
//            return;
//        }
//        if (countAdapter == null) {
//            count = new ListView(this);
//            countAdapter = new OrderGoodsCountAdapter(this, viewModel.getOrderStatisticList());
//            count.setAdapter(countAdapter);
//            printOrderFormStatisticsDialog = new AlertDialog.Builder(this)
//                    .setTitle(R.string.goodsCodeCount)
//                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
////                            orderGoodsCounting = false;
//                        }
//                    })
//                    .setView(count)
//                    .setCancelable(true)
//                    .create();
//        } else {
//            countAdapter.notifyDataSetChanged();
//        }
//        printOrderFormStatisticsDialog.show();
//    }
}
