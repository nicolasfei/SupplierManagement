package com.nicolas.supplier.server.common;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.supplier.server.CommandVo;

public class GoodsClassQuery extends CommonInterface {
    @Override
    public String getUrlParam() {
        return GoodsClassQuery;
    }

    @Override
    public String echo(CommandVo vo) {
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode,vo.token, vo.contentType);
    }
}
