package com.rcloud.server.sealtalk.exception;

import com.rcloud.server.sealtalk.constant.ErrorCode;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/9/18
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ServiceRuntimeException extends RuntimeException{

    private int errorCode;
    private String errorMessage;
    private int httpStatusCode;

    public ServiceRuntimeException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
    }

    public ServiceRuntimeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
    }

    public ServiceRuntimeException(ErrorCode errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
    }

    public ServiceRuntimeException(ErrorCode errorCode,Throwable cause) {
        super(cause);
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
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

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
