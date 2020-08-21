package com.nicolas.supplier.common;

public class OperateError {
    private String errorMsg;
    private Integer errorCode;
    private Object parameter;

    public OperateError(int errorCode, String errorMsg,Object parameter) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.parameter = parameter;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Object getParameter() {
        return parameter;
    }
}
