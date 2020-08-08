package com.rcloud.server.sealtalk.rongcloud;


import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import io.rong.models.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description: 融云服务调用模版
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
public class RongCloudInvokeTemplate {

    public static <T> T getData(RongCloudCallBack<T> action) throws ServiceException {

        try {
            T t = action.doInvoker();
            if (t instanceof Result) {
                Result result = (Result) t;
                if (result.getCode() == 200) {
                    return (T) result;
                } else {
                    log.error("invoke rongcloud exception,resultCode={},errorMessage={}", result.getCode(), result.getErrorMessage());
                    throw new ServiceException(result.getCode(), "RongCloud Server API Error Code: " + result.getCode());
                }
            } else {
                throw new RuntimeException("invoker rongcloud server error");
            }

        } catch (Exception e) {
            log.error("调用融云服务异常：" + e.getMessage(), e);
            throw new ServiceException(ErrorCode.INVOKE_RONG_CLOUD_SERVER_ERROR.getErrorCode(), ErrorCode.INVOKE_RONG_CLOUD_SERVER_ERROR.getErrorMessage() + e.getMessage());
        }
    }
}
