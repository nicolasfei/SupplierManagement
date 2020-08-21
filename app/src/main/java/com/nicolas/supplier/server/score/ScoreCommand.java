package com.nicolas.supplier.server.score;

import com.nicolas.supplier.server.Command;
import com.nicolas.supplier.server.CommandTypeEnum;
import com.nicolas.supplier.server.CommandVo;

import static com.nicolas.supplier.server.CommandTypeEnum.COMMAND_SUPPLIER_SCORE_RECORD;

public class ScoreCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        ScoreInterface recordQuery = new ScoreRecordQuery();
        ScoreInterface scoreClass = new ScoreClassQuery();

        recordQuery.setNextHandler(scoreClass);
        scoreClass.setNextHandler(null);
        super.firstNode = recordQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_SUPPLIER_SCORE_RECORD;
    }
}
