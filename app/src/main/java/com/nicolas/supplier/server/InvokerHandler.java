package com.nicolas.supplier.server;

import android.text.TextUtils;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.server.common.CommonInterface;
import com.nicolas.supplier.server.login.LoginInterface;
import com.nicolas.supplier.server.management.ManagementInterface;

import java.util.HashMap;
import java.util.Map;

public class InvokerHandler {

    private static InvokerHandler handler = new InvokerHandler();

    private InvokerHandler() {
    }

    public static InvokerHandler getInstance() {
        return handler;
    }

    /**
     * 获取命令---获取供应商信息
     *
     * @return 供应商信息命令
     */
    public CommandVo getSupplierInformationCommand() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;
        vo.url = ManagementInterface.SupplierInformation;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取货物类别
     *
     * @return 货物类别命令
     */
    public CommandVo getGoodsClassCommand(String name) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.GoodsClassQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        if (!TextUtils.isEmpty(name)) {
            parameters.put("name", name);           //选填
        }
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取库房信息
     *
     * @return 库房信息命令
     */
    public CommandVo getStorehouseInformationCommand() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.StorehouseQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "16");
        parameters.put("pageCount", "0");
//        parameters.put("code","1");         //库房编号---选填
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取库房信息
     *
     * @return 库房信息命令
     */
    public CommandVo getStorehouseInformationCommand(int currentPage, int pageSize) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.StorehouseQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(currentPage));
        parameters.put("pageSize", String.valueOf(pageSize));
        parameters.put("pageCount", "0");
//        parameters.put("code","1");         //库房编号---选填
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取分店信息
     *
     * @return 分店信息命令
     */
    public CommandVo getBranchInformationCommand() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.BranchQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "16");
        parameters.put("pageCount", "0");
//        parameters.put("fId","1");         //分店编号---选填
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取分店信息
     *
     * @return 分店信息命令
     */
    public CommandVo getBranchInformationCommand(int currentPage, int pageSize) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_COMMON;
        vo.url = CommonInterface.BranchQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(currentPage));
        parameters.put("pageSize", String.valueOf(pageSize));
        parameters.put("pageCount", "0");
//        parameters.put("fId","1");         //分店编号---选填
        vo.parameters = parameters;
        return vo;
    }
}
