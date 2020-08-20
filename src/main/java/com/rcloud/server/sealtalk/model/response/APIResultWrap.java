package com.rcloud.server.sealtalk.model.response;

import com.google.common.collect.ImmutableMap;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class APIResultWrap {

    public static final String SUCCESS_CODE = "200";

    public static final String DATA_RESULT = "result";

    private APIResultWrap() {
    }

    public static <T> APIResult<T> ok(T data) {
        return new APIResult<>(SUCCESS_CODE, StringUtils.EMPTY, data);
    }

    public static <T> APIResult<T> ok(T data,String message) {
        return new APIResult<>(SUCCESS_CODE, message, data);
    }

//    public static <T> APIResult<List<T>> ok(List<T> data) {
//        Map<String, List<T>> items = ImmutableMap.of(DATA_RESULT, data);
//        return new APIResult(SUCCESS_CODE, StringUtils.EMPTY, items);
//    }

    public static APIResult error(ServiceException ex) {
        int errorCode = ex.getErrorCode();
        String errorMessage = ex.getErrorMessage();

        return new APIResult<>(String.valueOf(errorCode),
            errorMessage);
    }

    public static APIResult error(ErrorCode errorCode) {
        return new APIResult<>(String.valueOf(errorCode.getErrorCode()),
            errorCode.getErrorMessage());
    }

    public static APIResult error(int code, String msg) {
        return new APIResult(String.valueOf(code), msg);
    }

    public static APIResult error(String code, String msg) {
        return new APIResult(code, msg);
    }
}
