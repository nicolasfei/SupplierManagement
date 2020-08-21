package com.nicolas.supplier.server.login;


import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;
import com.nicolas.supplier.server.management.SupplierAccountInformation;
import com.nicolas.supplier.server.management.SupplierInformation;

public class LoginCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        LoginInterface login = new UserLogin();
        LoginInterface logout = new UserLogout();

        login.setNextHandler(logout);
        logout.setNextHandler(null);

        super.firstNode = login;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_SUPPLIER_LOGIN;
    }
}
