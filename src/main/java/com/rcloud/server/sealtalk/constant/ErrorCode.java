package com.rcloud.server.sealtalk.constant;

public enum ErrorCode {
    COOKIE_ERROR(404, "Invalid cookie value!"),
    LIMIT_ERROR(5000, "Throttle limit exceeded."),
    SEND_SMS_ERROR(401, "Send sms error."),
    SERVER_ERROR(500, "Server error."),
    PARAM_ERROR(400,"缺少参数，请检查。"),
    YUN_PIAN_SMS_ERROR(3004,"Too many times sent"),
    TPL_FAILED_ERROR(3001,"Failed to get YunPian template"),
    REQUEST_ERROR(400, "错误的请求");


    private int errorCode;
    private String errorMessage;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
