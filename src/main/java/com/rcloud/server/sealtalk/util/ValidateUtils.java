package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;

import java.util.UUID;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 校验util
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ValidateUtils {

    public static void checkCompletePhone(String completePhone) throws ServiceException {

        if (!RegexUtils.checkMobile(completePhone)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }

    public static void checkRegion(String region) throws ServiceException {
        if (!Constants.REGION_NUM.equals(region)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }


    public static boolean checkUUIDStr(String str) {
        try {
            UUID.fromString(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
