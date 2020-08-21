package com.nicolas.supplier.server.order;

import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;

import static com.nicolas.supplier.server.CommandTypeEnum.COMMAND_SUPPLIER_ORDER;

public class OrderCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        OrderInterface query = new GoodsOrder();
        OrderInterface print = new GoodsOrderPrint();
        OrderInterface val = new GoodsOrderVal();

        query.setNextHandler(print);
        print.setNextHandler(val);
        val.setNextHandler(null);

        super.firstNode = query;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_SUPPLIER_ORDER;
    }
}
