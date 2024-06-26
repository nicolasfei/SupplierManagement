package com.nicolas.supplier.supplier;

import android.text.TextUtils;
import android.util.Log;

import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.supplier.data.BranchInformation;
import com.nicolas.supplier.data.StorehouseInformation;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.Invoker;
import com.nicolas.supplier.server.InvokerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SupplierKeeper {

    private String token;        //token
    private static SupplierKeeper keeper = new SupplierKeeper();

    private Supplier supplier;
    private SupplierAccount account;

    private TreeNode goodsClassTree;        //货物类型树
    private TreeNode storehouseTree;        //库房树
    private List<BranchInformation> branchList = new ArrayList<>();     //分店信息
    private List<String> branchCodeList = new ArrayList<>();            //分店编号信息
    private HashMap<String, String> goodsTypeMap = new HashMap<>();     //货号类型

    private InformationObserveTask timerTask = new InformationObserveTask();    //信息定时查询任务
    private boolean timerTaskIsStart = false;

    private SupplierKeeper() {
        this.supplier = new Supplier();
        this.account = new SupplierAccount();
    }

    public static SupplierKeeper getInstance() {
        return keeper;
    }

    public Supplier getOnDutySupplier() {
        return supplier;
    }

    public SupplierAccount getSupplierAccount() {
        return account;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public TreeNode getGoodsClassTree() {
        synchronized (SupplierKeeper.class) {
            return goodsClassTree;
        }
    }

    public TreeNode getStorehouseTree() {
        synchronized (Supplier.class) {
            return storehouseTree;
        }
    }

    public List<BranchInformation> getBranchList() {
        synchronized (Supplier.class) {
            return branchList;
        }
    }

    public String getBranchID(String fId) {
        for (BranchInformation b : branchList) {
            if (b.fId.equals(fId)) {
                return b.id;
            }
        }
        return "";
    }

    public String[] getGoodsTypeShow() {
        String[] goodsType = new String[this.goodsTypeMap.size()];
        int i = 0;
        for (Map.Entry<String, String> set : this.goodsTypeMap.entrySet()) {
            goodsType[i] = set.getValue();
            i++;
        }
        return goodsType;
    }

    public String getGoodsTypeRequest(String value) {
        for (Map.Entry<String, String> set : this.goodsTypeMap.entrySet()) {
            if (value.equals(set.getValue())) {
                return set.getKey();
            }
        }
        return "";
    }

    public void startTimerTask() {
        if (!this.timerTaskIsStart) {
            this.timerTask.start();
            this.timerTaskIsStart = true;
        }
    }

    public void cancelTimerTask() {
        if (this.timerTaskIsStart) {
            this.timerTask.cancel();
            this.timerTaskIsStart = false;
        }
    }

    public List<String> getBranchCodeList() {
        synchronized (Supplier.class) {
            return branchCodeList;
        }
    }

    public void clearGoodsClassSelect() {
        this.goodsClassTree.setSelectStatus(TreeNode.NO_SELECT);
    }

    public void clearStorehouseSelect() {
        this.storehouseTree.setSelectStatus(TreeNode.NO_SELECT);
    }

    private class InformationObserveTask {
        private long PERIOD = 300 * 60 * 1000;       //30分钟执行一次
        private Timer timer = new Timer();

        private void start() {
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //查询商品分类
                    CommandVo goodsVo = InvokerHandler.getInstance().getGoodsClassCommand(null);
                    String goodClass = Invoker.getInstance().synchronousExec(goodsVo);
                    TreeNode goodsTree;
                    try {
                        JSONObject object = new JSONObject(goodClass);
                        if (object.getBoolean("success")) {
                            if (object.has("jsonData")) {
                                goodsTree = TreeNode.buildTree(object.getString("jsonData"));
                                synchronized (SupplierKeeper.class) {
                                    goodsClassTree = goodsTree;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //查询库房
                    CommandVo storehouseVo = InvokerHandler.getInstance().getStorehouseInformationCommand();
                    String storehouse = Invoker.getInstance().synchronousExec(storehouseVo);
                    try {
                        JSONObject object = new JSONObject(storehouse);
                        if (object.getBoolean("success")) {
                            if (object.has("jsonData")) {
                                synchronized (SupplierKeeper.class) {
                                    storehouseTree = StorehouseInformation.buildTree(object.getString("jsonData"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //查询分店
                    int currentPage = 1;
                    int pageSize = 200;
                    int totalPage = 1000;
                    boolean queryFailed = false;
                    List<BranchInformation> branchInformationList = new ArrayList<>();
                    try {
                        for (int i = currentPage; i <= (totalPage % pageSize == 0 ? (totalPage / pageSize) : (totalPage / pageSize + 1)); i++) {
                            CommandVo branchVo = InvokerHandler.getInstance().getBranchInformationCommand(i, pageSize);
                            String branch = Invoker.getInstance().synchronousExec(branchVo);
                            if (TextUtils.isEmpty(branch)) {
                                queryFailed = true;
                                break;
                            }
                            JSONObject object = new JSONObject(branch);
                            if (object.getBoolean("success")) {
                                JSONArray array = object.getJSONArray("data");
                                for (int j = 0; j < array.length(); j++) {
                                    branchInformationList.add(new BranchInformation(array.getString(j)));
                                }
                                totalPage = object.getInt("total"); //获取总页数
                            }
                        }
                        if (!queryFailed && (branchInformationList.size() > 0)) {
                            synchronized (SupplierKeeper.class) {
                                branchList.clear();
                                branchList.addAll(branchInformationList);
                                branchCodeList.clear();
                                for (BranchInformation branch : branchInformationList) {
                                    branchCodeList.add(branch.fId);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //查询货号类型
                    CommandVo goodsTypeVo = InvokerHandler.getInstance().getGoodsTypeCommand();
                    String goodsType = Invoker.getInstance().synchronousExec(goodsTypeVo);
                    try {
                        Log.d("----------------->", "run: "+goodsType);
                        JSONObject object = new JSONObject(goodsType);
                        if (object.getBoolean("success")) {
                            if (object.has("kvData")) {
                                synchronized (SupplierKeeper.class) {
                                    JSONArray array = new JSONArray(object.getString("kvData"));
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject o = new JSONObject(array.getString(i));
                                        goodsTypeMap.put(o.getString("Key"), o.getString("Value"));
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, PERIOD);
        }

        private void cancel() {
            this.timer.cancel();
        }
    }
}

