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
        OrderInterface propertyQuery = new GoodsPropertyOrder();
        OrderInterface print = new GoodsOrderPrint();
        OrderInterface printAll = new GoodsOrderPrintAll();
        OrderInterface val = new GoodsOrderVal();
        OrderInterface inValid = new GoodsOrderInValid();
        OrderInterface distribution = new GoodsOrderDistribution();
        OrderInterface orderSubmit = new GoodsOrderSwait();

        query.setNextHandler(propertyQuery);
        propertyQuery.setNextHandler(print);
        print.setNextHandler(printAll);
        printAll.setNextHandler(val);
        val.setNextHandler(inValid);
        inValid.setNextHandler(distribution);
        distribution.setNextHandler(orderSubmit);
        orderSubmit.setNextHandler(null);

        super.firstNode = query;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_SUPPLIER_ORDER;
    }
}
