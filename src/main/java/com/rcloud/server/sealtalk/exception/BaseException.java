package com.rcloud.server.sealtalk.exception;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import lombok.Getter;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Getter
public class BaseException extends Exception {

    private int errorCode;
    private String errorMessage;
    private int httpStatusCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
    }

    public BaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();
    }

    public BaseException(String message, int errorCode,String errorMessage,int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatusCode=httpStatusCode;
    }

    public BaseException(String message, int errorCode,String errorMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
