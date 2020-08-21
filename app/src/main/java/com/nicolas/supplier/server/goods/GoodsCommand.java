package com.nicolas.supplier.server.goods;

import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;

import static com.nicolas.supplier.server.CommandTypeEnum.COMMAND_SUPPLIER_GOODS_ID;

public class GoodsCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        GoodsQuery query = new GoodsQuery();
        ReturnGoodsQuery returnGoodsQuery = new ReturnGoodsQuery();
        GoodsUpdate update = new GoodsUpdate();
        GoodsPropertyUpdate propertyUpdate = new GoodsPropertyUpdate();

        query.setNextHandler(returnGoodsQuery);
        returnGoodsQuery.setNextHandler(update);
        update.setNextHandler(propertyUpdate);
        propertyUpdate.setNextHandler(null);
        super.firstNode = query;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_SUPPLIER_GOODS_ID;
    }
}
