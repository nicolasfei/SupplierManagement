package com.nicolas.supplier.common;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class OperateResult {
    @Nullable
    private OperateInUserView success;
    @Nullable
    private OperateError error;

    public OperateResult(@Nullable OperateError error) { this.error = error; }

    public OperateResult(@Nullable OperateInUserView success) {
        this.success = success;
    }

    @Nullable
    public OperateInUserView getSuccess() {
        return success;
    }

    @Nullable
    public OperateError getError() {
        return error;
    }
}
