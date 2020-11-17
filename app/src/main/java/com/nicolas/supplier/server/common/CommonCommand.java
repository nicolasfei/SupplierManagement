package com.nicolas.supplier.server.common;

import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;

public class CommonCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        CommonInterface goodsClassQuery = new GoodsClassQuery();
        CommonInterface branchQuery = new BranchQuery();
        CommonInterface storehouseQuery = new StorehouseQuery();
        CommonInterface goodType = new GoodsType();

        CommonInterface versionCheck = new VersionCheck();
        CommonInterface noticeCheck = new NoticeCheck();

        CommonInterface statisticsGoodsId = new StatisticsGoodsId();
        CommonInterface statisticsGoodsIdTotal = new StatisticsGoodsIdTotal();

        goodsClassQuery.setNextHandler(branchQuery);
        branchQuery.setNextHandler(storehouseQuery);
        storehouseQuery.setNextHandler(goodType);
        goodType.setNextHandler(versionCheck);
        versionCheck.setNextHandler(noticeCheck);
        noticeCheck.setNextHandler(statisticsGoodsId);
        statisticsGoodsId.setNextHandler(statisticsGoodsIdTotal);
        statisticsGoodsIdTotal.setNextHandler(null);

        super.firstNode = goodsClassQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_COMMON;
    }
}
