package com.nicolas.supplier.server.score;

import com.nicolas.supplier.server.AbstractInterface;

public abstract class ScoreInterface extends AbstractInterface {
    //信誉分记录查询接口
    public final static String ScoreRecordQuery = AbstractInterface.COMMAND_URL+"Supplier/ScoreRecord";
    //信誉分说明接口
    public final static String ScoreClassQuery = AbstractInterface.COMMAND_URL+"Supplier/ScoreClass";
}
