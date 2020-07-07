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
public class ResultWrap {

    public static final String SUCCESS_CODE = "200";

    public static final String DATA_RESULT = "result";

    private ResultWrap() {
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(SUCCESS_CODE, StringUtils.EMPTY, data);
    }

    public static <T> Response<List<T>> ok(List<T> data) {
        Map<String, List<T>> items = ImmutableMap.of(DATA_RESULT, data);
        return new Response(SUCCESS_CODE, StringUtils.EMPTY, items);
    }

    public static Response error(ServiceException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return new Response<>(String.valueOf(errorCode.getErrorCode()),
            errorCode.getErrorMessage());
    }

    public static Response error(ErrorCode errorCode) {
        return new Response<>(String.valueOf(errorCode.getErrorCode()),
            errorCode.getErrorMessage());
    }

    public static Response error(int code, String msg) {
        return new Response(String.valueOf(code), msg);
    }

    public static Response error(String code, String msg) {
        return new Response(code, msg);
    }
}
