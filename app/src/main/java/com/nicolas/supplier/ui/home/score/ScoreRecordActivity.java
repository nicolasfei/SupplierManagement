package com.nicolas.supplier.ui.home.score;

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
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.supplier.R;
import com.nicolas.supplier.common.OperateResult;
import com.nicolas.supplier.data.ScoreRecordAdapter;
import com.nicolas.supplier.supplier.SupplierKeeper;
import com.nicolas.supplier.ui.BaseActivity;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreRecordActivity extends BaseActivity implements View.OnClickListener {

    private ScoreRecordViewModel viewModel;
    private DrawerLayout drawerLayout;
    private TextView staff;
    //查询条件
    private TextView recordTime;
    private TextView scoreClassId;
    private TextView newGoodsID;
    private PullRefreshListView pullToRefreshListView;
    private ScoreRecordAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_record_query);
        this.viewModel = new ViewModelProvider(this).get(ScoreRecordViewModel.class);
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

        this.recordTime = findClickView(R.id.recordTime);
        this.recordTime.setClickable(true);
        this.updateRecordTime(this.viewModel.getRecordTime().replace("~", "\u3000~\u3000"));

        this.scoreClassId = findClickView(R.id.scoreClassId);
        this.scoreClassId.setClickable(true);

        this.newGoodsID = findClickView(R.id.newGoodsID);
        this.newGoodsID.setClickable(true);

        this.pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new ScoreRecordAdapter(this, viewModel.getScoreRecordList());
        this.pullToRefreshListView.setAdapter(adapter);
        this.pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshScoreRecord();
            }
        });
        this.pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreScoreRecord();
            }
        });

        findClickView(R.id.recordTimeClear);
        findClickView(R.id.scoreClassIdClear);
        findClickView(R.id.newGoodsIDClear);
        findClickView(R.id.query);
        findClickView(R.id.reset);

        /**
         * 监听查询结果
         */
        this.viewModel.getScoreRecordQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showAlertDialog(ScoreRecordActivity.this, getString(R.string.success),
                                (String) msg.obj, new BruceDialog.OnAlertDialogListener() {
                                    @Override
                                    public void onSelect(boolean confirm) {

                                    }
                                });
                    }
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(ScoreRecordActivity.this, getString(R.string.failed),
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
                String queryDate = viewModel.getRecordTime();
                if (TextUtils.isEmpty(queryDate)) {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                    queryDate = nowDate + "~" + nowDate;
                }
                pullToRefreshListView.updateContentDate(queryDate);
            }
        });

        showProgressDialog(getString(R.string.querying));
        this.viewModel.queryScoreRecord();
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
            case R.id.recordTime:
                String time = viewModel.getRecordTime();
                String start = "";
                String end = "";
                if (!TextUtils.isEmpty(time)) {
                    String[] times = time.split("~");
                    if (times.length >= 2) {
                        start = times[0];
                        end = times[1];
                    }
                }
                DateTimePickerDialog.showDateSlotPickerDialog(this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
                    @Override
                    public void OnDateTimeSlotPick(String start, String end) {
                        if (!TextUtils.isEmpty(start)) {
                            viewModel.setRecordTime((start + "~" + end));
                            updateRecordTime((start + "\u3000~\u3000" + end));
                        }
                    }
                });
                break;
            case R.id.scoreClassId:
                BruceDialog.showEditInputDialog(R.string.scoreClassId, R.string.scoreClassId, InputType.TYPE_CLASS_TEXT, this,
                        new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                if (!TextUtils.isEmpty(itemName)) {
                                    viewModel.setScoreClassId(itemName);
                                    updateScoreClassId(itemName);
                                }
                            }
                        });
                break;
            case R.id.newGoodsID:
                BruceDialog.showEditInputDialog(R.string.newGoodsID, R.string.newGoodsID, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            viewModel.setGoodsId(itemName);
                            updateNewGoodsID(itemName);
                        }
                    }
                });
                break;
            case R.id.recordTimeClear:
                recordTimeClear();
                break;
            case R.id.scoreClassIdClear:
                scoreClassIdClear();
                break;
            case R.id.newGoodsIDClear:
                newGoodsIDClear();
                break;
            case R.id.query:
                showProgressDialog( getString(R.string.querying));
                drawerLayout.closeDrawer(Gravity.RIGHT, true);
                viewModel.queryScoreRecord();
                break;
            case R.id.reset:
                resetQueryCondition();
                break;
            default:
                break;
        }
    }

    private void newGoodsIDClear() {
        this.viewModel.setGoodsId("");
        this.updateNewGoodsID("");
    }

    private void scoreClassIdClear() {
        this.viewModel.setScoreClassId("");
        this.updateScoreClassId("");
    }

    private void recordTimeClear() {
        this.viewModel.setRecordTime(Tool.getNearlyThreeDaysDateSlot());
        this.updateRecordTime(this.viewModel.getRecordTime().replace("~", "\u3000~\u3000"));
    }

    private void updateStaff(String staff) {
        String value = getString(R.string.staff) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + staff + "</font>";
        this.staff.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateNewGoodsID(String newGoodsID) {
        String value = getString(R.string.newGoodsID) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + newGoodsID + "</font>";
        this.newGoodsID.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateScoreClassId(String scoreClassId) {
        String value = getString(R.string.scoreClassId) + "\u3000\u3000" + "<font color=\"black\">" + scoreClassId + "</font>";
        this.scoreClassId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void updateRecordTime(String recordTime) {
        String value = "<font color=\"black\">" + recordTime + "</font>";
        this.recordTime.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 重置查询条件
     */
    private void resetQueryCondition() {
        this.viewModel.resetQueryCondition();
        this.updateNewGoodsID("");
        this.updateScoreClassId("");
        this.updateRecordTime(this.viewModel.getRecordTime().replace("~", "\u3000~\u3000"));
    }
}
