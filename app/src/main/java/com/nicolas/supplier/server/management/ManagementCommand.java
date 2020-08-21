package com.nicolas.supplier.server.management;

import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;

import static com.nicolas.supplier.server.CommandTypeEnum.COMMAND_SUPPLIER_MANAGEMENT;

public class ManagementCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        //供应商信息，账户信息，登陆记录
        ManagementInterface supplierInformation = new SupplierInformation();
        ManagementInterface supplierAccount = new SupplierAccountInformation();
        ManagementInterface supplierLoginQuery = new SupplierLoginQuery();
        ManagementInterface supplierPassModify = new SupplierPassModify();

        //供应商下属员工的增删改查，暂未开放
        ManagementInterface add = new UserHandlerAdd();
        ManagementInterface del = new UserHandlerDel();
        ManagementInterface update = new UserHandlerUpdate();
        ManagementInterface query = new UserHandlerQuery();

        supplierInformation.setNextHandler(supplierAccount);
        supplierAccount.setNextHandler(supplierLoginQuery);
        supplierLoginQuery.setNextHandler(supplierPassModify);
        supplierPassModify.setNextHandler(add);

        add.setNextHandler(del);
        del.setNextHandler(update);
        update.setNextHandler(query);
        query.setNextHandler(null);

        super.firstNode = supplierInformation;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_SUPPLIER_MANAGEMENT;
    }
}
