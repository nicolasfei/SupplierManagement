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
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.datetimepickerlibrary.DateTimePickerDialog;
import com.nicolas.multileveltreelibrary.TreeNode;
import com.nicolas.multileveltreelibrary.TreeNodeViewDialog;
import com.nicolas.pullrefreshlibrary.PullRefreshListView;
import com.nicolas.supplier.ui.home.returngoods.ReturnGoodsQueryActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.GoodsCodeAdapter;
import com.nicolas.supplier.data.GoodsCodeClass;
import com.nicolas.supplier.data.GoodsOrderStatus;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoodsQueryActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoodsQueryActivity";

    private GoodsQueryViewModel viewModel;
    private DrawerLayout drawerLayout;
    private TextView staff;
    private TextView goodsClassId;              //货物类别ID
    private TextView goodsId;                   //货号
    private TextView oldGoodsId;                //旧货号
    private RadioGroup goodsTypeGroup;          //货号类型(normal正常/attempt试卖/replace代卖/special特殊需求)
    //private RadioButton normal, attempt, replace, special;
    private TextView createTime;                //创建时间
    private RadioGroup isStockGroup;            //允许下单
    //private RadioButton allow, forbid;


    private PullRefreshListView pullToRefreshListView;
    private GoodsCodeAdapter adapter;

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
        this.staff.setOnClickListener(this);
        this.staff.setClickable(true);
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        this.goodsClassId = findViewById(R.id.goodsClassId);
        this.goodsClassId.setOnClickListener(this);
        this.goodsClassId.setClickable(true);

        this.goodsId = findViewById(R.id.goodsId);
        this.goodsId.setOnClickListener(this);
        this.goodsId.setClickable(true);

        this.oldGoodsId = findViewById(R.id.oldGoodsId);
        this.oldGoodsId.setOnClickListener(this);
        this.oldGoodsId.setClickable(true);

        this.goodsTypeGroup = findViewById(R.id.goodsTypeGroup);
        this.goodsTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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

        this.createTime = findViewById(R.id.createTime);
        this.createTime.setOnClickListener(this);
        this.createTime.setClickable(true);

        this.isStockGroup = findViewById(R.id.isStockGroup);
        this.isStockGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
                BruceDialog.showProgressDialog(GoodsQueryActivity.this, getString(R.string.querying));
                String stock = isStock ? GoodsOrderStatus.Allow : GoodsOrderStatus.Forbid;
                if (TextUtils.isEmpty(goodsCodePropertyID)) {
                    viewModel.updateGoodsStatus(goodsCodeID, stock);
                } else {
                    viewModel.updateGoodsPropertyStatus(goodsCodeID, goodsCodePropertyID, stock);
                }
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

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(this);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        /**
         * 监听货号查询结果
         */
        this.viewModel.getQueryGoodsIDResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        Utils.toast(GoodsQueryActivity.this, (String) msg.obj);
                    }
                }
                if (operateResult.getError() != null) {
                    Utils.toast(GoodsQueryActivity.this, operateResult.getError().getErrorMsg());
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
            }
        });

        /**
         * 监听货号下单状态更新
         */
        this.viewModel.getUpdateGoodsStatusResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                adapter.notifyDataSetChanged();
                if (operateResult.getSuccess() != null) {
                    Utils.toast(GoodsQueryActivity.this, R.string.setSuccess);
                }
                if (operateResult.getError() != null) {
                    Utils.toast(GoodsQueryActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        //默认查询
        BruceDialog.showProgressDialog(this, getString(R.string.querying));
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
                            viewModel.queryCondition.setGoodsClassId(itemName);
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
                DateTimePickerDialog.showDateSlotPickerDialog(this, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
                            viewModel.queryCondition.setOldGoodsId((start + "~" + end));
                            updateCreateTime(start + "\u3000~\u3000" + end);
                        }
                    }
                });
                break;

            case R.id.reset:
                queryConditionReset();
                break;
            case R.id.submit:
                this.goodsConditionQuery();
                break;
            default:
                break;
        }
    }

    /**
     * 条件查询
     */
    private void goodsConditionQuery() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        if (viewModel.queryCondition.isQueryConditionUpdate()) {
            BruceDialog.showProgressDialog(this, getString(R.string.querying));
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
     * 重置所有查询条件
     */
    private void queryConditionReset() {
        this.updateGoodsClassId("");
        this.updateGoodsID("");
        this.updateOldGoodsID("");
        this.goodsTypeGroup.clearCheck();
        this.updateCreateTime("");
        this.isStockGroup.clearCheck();
        this.viewModel.queryCondition.clear();
        SupplierKeeper.getInstance().clearGoodsClassSelect();
    }
}
