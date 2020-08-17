package com.rcloud.server.sealtalk.exception;

import com.rcloud.server.sealtalk.constant.ErrorCode;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ServiceException extends BaseException {

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage(), errorCode);
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public ServiceException(int errorCode,String errorMessage,int httpStatusCode){
        super(errorMessage,errorCode,errorMessage,httpStatusCode);
    }

    public ServiceException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
