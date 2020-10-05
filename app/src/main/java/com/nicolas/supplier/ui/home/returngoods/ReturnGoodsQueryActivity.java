package com.nicolas.supplier.ui.home.returngoods;

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
import com.nicolas.supplier.data.ReturnGoodsInformationAdapter;
import com.nicolas.supplier.ui.home.goods.GoodsQueryActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReturnGoodsQueryActivity extends BaseActivity implements View.OnClickListener {

    private ReturnGoodsQueryViewModel viewModel;
    private DrawerLayout drawerLayout;
    private TextView staff;
    //查询条件
    private TextView goodsClassId;
    private TextView fId;
    private TextView oldGoodsId;
    private TextView goodsId;
    private TextView checkTime;
    private TextView barcodeId;                 //条码查询
    //list
    private PullRefreshListView pullToRefreshListView;
    private ReturnGoodsInformationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_goods_query);
        this.viewModel = new ViewModelProvider(this).get(ReturnGoodsQueryViewModel.class);
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
        this.updateStaff(SupplierKeeper.getInstance().getOnDutySupplier().name);

        this.goodsClassId = findClickView(R.id.goodsClassId);
        this.goodsClassId.setClickable(true);

        this.fId = findClickView(R.id.fId);
        this.fId.setClickable(true);

        this.oldGoodsId = findClickView(R.id.oldGoodsId);
        this.oldGoodsId.setClickable(true);

        this.goodsId = findClickView(R.id.goodsId);
        this.goodsId.setClickable(true);

        this.barcodeId = findClickView(R.id.barcodeId);
        this.barcodeId.setClickable(true);

        this.checkTime = findClickView(R.id.checkTime);
        this.checkTime.setClickable(true);
        this.updateCheckTime(this.viewModel.queryCondition.checkTime.replace("~", "\u3000~\u3000"));       //默认是近3天

        this.pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new ReturnGoodsInformationAdapter(this, viewModel.getReturnGoodsInformationList());
        this.pullToRefreshListView.setAdapter(adapter);
        this.pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreData();
            }
        });
        this.pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
            }
        });

        Button button = findClickView(R.id.query);
        Button button1 = findClickView(R.id.reset);

        //监听查询结果
        this.viewModel.getReturnGoodsQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showAlertDialog(ReturnGoodsQueryActivity.this, getString(R.string.success),
                                (String) msg.obj, new BruceDialog.OnAlertDialogListener() {
                                    @Override
                                    public void onSelect(boolean confirm) {

                                    }
                                });
                    }
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(ReturnGoodsQueryActivity.this, getString(R.string.failed),
                            operateResult.getError().getErrorMsg(), new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                }
                if (pullToRefreshListView.isPullToRefreshing()) {
                    pullToRefreshListView.refreshFinish();
                }
                if (pullToRefreshListView.isPushLoadingMore()) {
                    pullToRefreshListView.loadMoreFinish();
                }
                //更新查询日期
                String queryDate = viewModel.queryCondition.checkTime;
                if (TextUtils.isEmpty(queryDate)) {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                    queryDate = nowDate + "~" + nowDate;
                }
                pullToRefreshListView.updateContentDate(queryDate);
            }
        });

        //查询返货
        BruceDialog.showProgressDialog(this, getString(R.string.querying));
        viewModel.queryReturnGoods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.return_goods_menu, menu);
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
                TreeNodeViewDialog.showTreeNodeViewDialog(this, getString(R.string.goodsClassChoice), SupplierKeeper.getInstance().getGoodsClassTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                    @Override
                    public void onChoice(List<TreeNode> node) {
                        if (node != null && node.size() > 0) {
                            viewModel.queryCondition.goodsClassId = node.get(0).id;
                            updateGoodsClassId(node.get(0).name);
                        }
                    }
                });
                break;
            case R.id.fId:
                BruceDialog.showAutoCompleteTextViewDialog(R.string.branchID, R.string.branchInput, InputType.TYPE_CLASS_TEXT, this,
                        SupplierKeeper.getInstance().getBranchCodeList(), new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                if (!TextUtils.isEmpty(itemName)) {
                                    viewModel.queryCondition.fId = itemName;
                                    updateBranchID(itemName);
                                }
                            }
                        });
                break;
            case R.id.oldGoodsId:
                BruceDialog.showEditInputDialog(R.string.oldGoodsID, R.string.oldGoodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.queryCondition.oldGoodsId = itemName;
                            updateOldGoodsId(itemName);
                        }
                    }
                });
                break;
            case R.id.goodsId:
                BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.queryCondition.goodsId = itemName;
                            updateGoodsId(itemName);
                        }
                    }
                });
                break;
            case R.id.checkTime:
                DateTimePickerDialog.showDateSlotPickerDialog(this, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        if (!TextUtils.isEmpty(start)) {
                            viewModel.queryCondition.checkTime = start + "~" + end;
                            updateCheckTime((start + "\u3000~\u3000" + end));
                        }
                    }
                });
                break;
            case R.id.barcodeId:
                BruceDialog.showEditInputDialog(R.string.barcodeId, R.string.barcodeIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.queryCondition.barcodeID = itemName;
                            updateBarcodeId(itemName);
                        }
                    }
                });
                break;
            case R.id.query:
                BruceDialog.showProgressDialog(this, getString(R.string.querying));
                drawerLayout.closeDrawer(Gravity.RIGHT, true);
                viewModel.queryReturnGoods();
                break;
            case R.id.reset:
                resetQueryCondition();
                break;
            default:
                break;
        }
    }

    private void updateStaff(String staff) {
        String value = getString(R.string.staff) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + staff + "</font>";
        this.staff.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsClassId(String goodsType) {
        String value = getString(R.string.goodsClass) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + goodsType + "</font>";
        this.goodsClassId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateBranchID(String branchID) {
        String value = getString(R.string.branchID) + "\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + branchID + "</font>";
        this.fId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateOldGoodsId(String oldGoodsId) {
        String value = getString(R.string.oldGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + oldGoodsId + "</font>";
        this.oldGoodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateGoodsId(String goodsId) {
        String value = getString(R.string.newGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + goodsId + "</font>";
        this.goodsId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateBarcodeId(String barcodeID) {
        String value = getString(R.string.barcodeId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + barcodeID + "</font>";
        this.barcodeId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateCheckTime(String checkTime) {
        String value = "<font color=\"black\">" + checkTime + "</font>";
        this.checkTime.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 重置查询条件
     */
    private void resetQueryCondition() {
        this.viewModel.resetQueryCondition();
        this.updateGoodsClassId("");
        this.updateBranchID("");
        this.updateOldGoodsId("");
        this.updateGoodsId("");
        this.updateBarcodeId("");
        this.updateCheckTime(this.viewModel.queryCondition.checkTime.replace("~", "\u3000~\u3000"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
