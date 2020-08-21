package com.nicolas.supplier.server;

public abstract class Command {

    protected AbstractInterface firstNode;

    public Command() {
        //建立责任链条
        this.buildDutyChain();
    }

    //执行命令
    public abstract String execute(CommandVo vo);
    //建立责任链表
    protected abstract void buildDutyChain();
    //获取命令类型
    public abstract CommandTypeEnum getCommandType();

//    //建立链表
//    protected final List<? extends AbstractInterface> buildChain(Class<? extends AbstractInterface> abstractClass) {
//        //取出所有的命令名下的子类
//        List<Class> classes = ClassUtils.getSonClass(abstractClass);
//        //存放命令的实例，并建立链表关系
//        List<AbstractInterface> commandList = new ArrayList<>();
//        for (Class c : classes) {
//            AbstractInterface command = null;
//            try {
//                command = (AbstractInterface) Class.forName(c.getName()).newInstance();
//            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            //建立链表
//            if (commandList.size() > 0) {
//                commandList.get(commandList.size() - 1).setNextCommand(command);
//            }
//            commandList.add(command);
//        }
//        return commandList;
//    }
}
