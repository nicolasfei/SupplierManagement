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
        CommonInterface versionCheck = new VersionCheck();

        goodsClassQuery.setNextHandler(branchQuery);
        branchQuery.setNextHandler(storehouseQuery);
        storehouseQuery.setNextHandler(versionCheck);
        versionCheck.setNextHandler(null);

        super.firstNode = goodsClassQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_COMMON;
    }
}
