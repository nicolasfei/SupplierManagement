package com.nicolas.supplier.ui.home.goods;

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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.multileveltree.TreeNodeViewDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.GoodsCodeAdapter;
import com.nicolas.supplier.data.GoodsCodeClass;
import com.nicolas.supplier.data.GoodsCodeQueryCondition;
import com.nicolas.supplier.data.GoodsOrderStatus;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoodsQueryActivity extends BaseActivity implements View.OnClickListener {

    private GoodsQueryViewModel viewModel;
    private DrawerLayout drawerLayout;
    private TextView staff;
    private TextView detailed;          //明细
    //条件查询
    private TextView goodsClassId;              //货物类别ID
    private TextView goodsId;                   //货号
    private TextView oldGoodsId;                //旧货号
    private RadioGroup goodsTypeGroup;          //货号类型(normal正常/attempt试卖/replace代卖/special特殊需求)

    private TextView createTime;                //创建时间
    private RadioGroup isStockGroup;            //允许下单

    private PullRefreshListView pullToRefreshListView;
    private GoodsCodeAdapter adapter;

    private boolean isClearGoodsType = false;   //是否手动清除-货号类型
    private boolean isClearStockStatus = false; //是否手动清除-货号类型

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_query);

        this.viewModel = new ViewModelProvider(this).get(GoodsQueryViewModel.class);
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

        this.staff = findViewById(R.id.staff);
//        this.staff.setOnClickListener(this);
//        this.staff.setClickable(true);
        this.detailed = findClickView(R.id.detailed);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        //---------------------条件查询----------------------//
        this.goodsClassId = findClickView(R.id.goodsClassId);
        this.goodsClassId.setClickable(true);

        this.goodsId = findClickView(R.id.goodsId);
        this.goodsId.setClickable(true);

        this.oldGoodsId = findClickView(R.id.oldGoodsId);
        this.oldGoodsId.setClickable(true);

        this.goodsTypeGroup = findViewById(R.id.goodsTypeGroup);
        this.goodsTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isClearGoodsType) {
                    isClearGoodsType = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.normal:
                        viewModel.queryCondition.setGoodsType(GoodsCodeClass.Normal);
                        break;
                    case R.id.attempt:
                        viewModel.queryCondition.setGoodsType(GoodsCodeClass.Attempt);
                        break;
                    case R.id.replace:
                        viewModel.queryCondition.setGoodsType(GoodsCodeClass.Replace);
                        break;
                    case R.id.special:
                        viewModel.queryCondition.setGoodsType(GoodsCodeClass.Special);
                        break;
                    default:
                        break;
                }
            }
        });

        this.createTime = findClickView(R.id.createTime);
        this.createTime.setClickable(true);
        this.updateCreateTime(viewModel.queryCondition.getCreateTime().replace("~", "\u3000~\u3000"));

        this.isStockGroup = findViewById(R.id.isStockGroup);
        this.isStockGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isClearStockStatus) {
                    isClearStockStatus = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.allow:
                        viewModel.queryCondition.setIsStock(GoodsOrderStatus.Allow);
                        break;
                    case R.id.forbid:
                        viewModel.queryCondition.setIsStock(GoodsOrderStatus.Forbid);
                        break;
                    default:
                        break;
                }
            }
        });

        this.pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new GoodsCodeAdapter(this, viewModel.getGoodsCodeList());
        this.adapter.setOnGoodsCodeStatusChangeListener(new GoodsCodeAdapter.OnGoodsCodeStatusChangeListener() {
            @Override
            public void OnGoodsCodeStockChange(String goodsCodeID, String goodsCodePropertyID, boolean isStock) {
                showProgressDialog( getString(R.string.GoodsStatusUpdate));
                String stock = isStock ? GoodsOrderStatus.Allow : GoodsOrderStatus.Forbid;
                if (TextUtils.isEmpty(goodsCodePropertyID)) {
                    viewModel.updateGoodsStatus(goodsCodeID, stock);
                } else {
                    viewModel.updateGoodsPropertyStatus(goodsCodeID, goodsCodePropertyID, stock);
                }
            }

            @Override
            public void OnGoodsCodePropertyQuery(String goodsCodeID) {
                //查询货号属性
                showProgressDialog( getString(R.string.GoodsPropertyQuery));
                viewModel.queryGoodsCodeProperty(goodsCodeID);
            }
        });
        this.pullToRefreshListView.setAdapter(adapter);
        this.pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
            }
        });
        this.pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreData();
            }
        });
//        this.pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    case SCROLL_STATE_IDLE:             //是当屏幕停止滚动时
//                        adapter.setIsBusy(false);
//                        break;
//                    case SCROLL_STATE_TOUCH_SCROLL:     //是当用户在以触屏方式滚动屏幕并且手指仍然还在屏幕上时
//                        break;
//                    case SCROLL_STATE_FLING:            //是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时--这个时候默认图片只在缓存中加载，如果缓存没有，这不下载
//                        adapter.setIsBusy(true);
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });

        findClickView(R.id.createTimeClear);
        findClickView(R.id.goodsClassIdClear);
        findClickView(R.id.goodsIdClear);
        findClickView(R.id.oldGoodsIdClear);
        findClickView(R.id.goodsTypeClear);
        findClickView(R.id.isStockClear);
        findClickView(R.id.reset);
        findClickView(R.id.submit);

        //监听货号查询结果
        this.viewModel.getQueryGoodsIDResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                                (String) msg.obj);
                    }
                    updateDetailed();           //更新订单明细
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                if (pullToRefreshListView.isPushLoadingMore()) {
                    pullToRefreshListView.loadMoreFinish();
                }
                if (pullToRefreshListView.isPullToRefreshing()) {
                    pullToRefreshListView.refreshFinish();
                }
                //更新查询日期
                String queryDate = viewModel.queryCondition.getCreateTime();
                if (TextUtils.isEmpty(queryDate)) {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                    queryDate = nowDate + "~" + nowDate;
                }
                pullToRefreshListView.updateContentDate(queryDate);
                dismissProgressDialog();
            }
        });

        //监听货号属性查询结果
        this.viewModel.getQueryGoodsPropertyResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                                (String) msg.obj);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //监听货号下单状态更新
        this.viewModel.getUpdateGoodsStatusResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                adapter.notifyDataSetChanged();
                if (operateResult.getSuccess() != null) {
                    BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                            getString(R.string.setSuccess));
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(GoodsQueryActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //默认查询
        showProgressDialog( getString(R.string.querying));
        this.viewModel.queryGoodsID();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.goods_code_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_query:
                this.drawerLayout.openDrawer(GravityCompat.END);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staff:
                break;
            case R.id.goodsClassId:
                TreeNodeViewDialog.showTreeNodeViewDialog(GoodsQueryActivity.this, getString(R.string.goodsClassChoice), SupplierKeeper.getInstance().getGoodsClassTree(),
                        false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                            @Override
                            public void onChoice(List<TreeNode> node) {
                                if (node != null && node.size() > 0) {
                                    viewModel.queryCondition.setGoodsClassId(node.get(0).id);
                                    updateGoodsClassId(node.get(0).name);
                                }
                            }
                        });
                break;
            case R.id.goodsId:
                BruceDialog.showEditInputDialog(R.string.goodsID, R.string.goodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.queryCondition.setGoodsId(itemName);
                            updateGoodsID(itemName);
                        }
                    }
                });
                break;
            case R.id.oldGoodsId:
                BruceDialog.showEditInputDialog(R.string.oldGoodsID, R.string.oldGoodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.queryCondition.setOldGoodsId(itemName);
                            updateOldGoodsID(itemName);
                        }
                    }
                });
                break;
            case R.id.createTime:
                GoodsCodeQueryCondition condition = viewModel.queryCondition;
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
                DateTimePickerDialog.showDateSlotPickerDialog(this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
                            viewModel.queryCondition.setCreateTime((start + "~" + end));
                            updateCreateTime(start + "\u3000~\u3000" + end);
                        }
                    }
                });
                break;
            case R.id.createTimeClear:
                createTimeReset();
                break;
            case R.id.goodsClassIdClear:
                goodsClassIdClear();
                break;
            case R.id.goodsIdClear:
                goodsIdClear();
                break;
            case R.id.oldGoodsIdClear:
                oldGoodsIdClear();
                break;
            case R.id.goodsTypeClear:
                goodsTypeClear();
                break;
            case R.id.isStockClear:
                isStockClear();
                break;
            case R.id.reset:
                queryConditionReset();
                break;
            case R.id.submit:
                goodsConditionQuery();
                break;
            default:
                break;
        }
    }

    private void isStockClear() {
        this.viewModel.queryCondition.setIsStock("");
        this.isClearStockStatus = true;
        this.isStockGroup.clearCheck();
    }

    private void goodsTypeClear() {
        this.viewModel.queryCondition.setGoodsType("");
        this.isClearGoodsType = true;       //先置位条件
        this.goodsTypeGroup.clearCheck();
    }

    private void oldGoodsIdClear() {
        this.viewModel.queryCondition.setOldGoodsId("");
        this.updateOldGoodsID("");
    }

    /**
     * 清空新货号
     */
    private void goodsIdClear() {
        this.viewModel.queryCondition.setGoodsId("");
        this.updateGoodsID("");
    }

    /**
     * 清空货物类型
     */
    private void goodsClassIdClear() {
        this.viewModel.queryCondition.setGoodsClassId("");
        this.updateGoodsClassId("");
        SupplierKeeper.getInstance().clearGoodsClassSelect();
    }

    /**
     * 重置创建时间
     */
    private void createTimeReset() {
        this.viewModel.queryCondition.setCreateTime(Tool.getNearlyThreeMonthDateSlot());
        this.updateCreateTime(this.viewModel.queryCondition.getCreateTime().replace("~", "\u3000~\u3000"));
    }

    /**
     * 条件查询
     */
    private void goodsConditionQuery() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        if (viewModel.queryCondition.isQueryConditionUpdate()) {
            showProgressDialog( getString(R.string.querying));
            viewModel.queryGoodsID();
            viewModel.queryCondition.resetQueryConditionUpdateStatus();
        }
    }

    private void updateStaff(String staff) {
        String value = getString(R.string.staff) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + staff + "</font>";
        this.staff.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsClassId(String goodsClassName) {
        String value = getString(R.string.goodsClass) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + goodsClassName + "</font>";
        this.goodsClassId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsID(String goodsID) {
        String value = getString(R.string.goodsID) + "\u3000\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + goodsID + "</font>";
        this.goodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateOldGoodsID(String oldGoodsID) {
        String value = getString(R.string.oldGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + oldGoodsID + "</font>";
        this.oldGoodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateCreateTime(String dateSlot) {
        String value = "<font color=\"black\">" + dateSlot + "</font>";
        this.createTime.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 更新明细
     */
    private void updateDetailed() {
        String value = getString(R.string.detailed) + "\u3000\u3000" + getString(R.string.goodsCodeTotal) + getString(R.string.colon) + "<font color=\"black\">" +
                this.viewModel.getGoodsCodeTotal() + "</font>";
        this.detailed.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 重置所有查询条件
     */
    private void queryConditionReset() {
        this.viewModel.queryCondition.clear();

        this.updateGoodsClassId("");
        this.updateGoodsID("");
        this.updateOldGoodsID("");
        this.isClearGoodsType = true;       //先置位条件
        this.goodsTypeGroup.clearCheck();
        this.updateCreateTime(this.viewModel.queryCondition.getCreateTime());
        this.isClearStockStatus = true;
        this.isStockGroup.clearCheck();

        SupplierKeeper.getInstance().clearGoodsClassSelect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
