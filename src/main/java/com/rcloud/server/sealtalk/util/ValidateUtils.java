package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import org.springframework.util.StringUtils;

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

    public static void checkRegionName(String regionName) throws ServiceException {
        if (!Constants.REGION_NAME.equals(regionName)) {
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

    public static void checkNickName(String nickname) throws ServiceException {
        if (org.springframework.util.StringUtils.isEmpty(nickname) || nickname.length() > 32) {
            throw new ServiceException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
    }

    public static void checkUUID(String verificationToken) throws ServiceException {
        if (!ValidateUtils.checkUUIDStr(verificationToken)) {
            throw new ServiceException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }
    }

    public static void checkPassword(String password) throws ServiceException {

        if (org.springframework.util.StringUtils.isEmpty(password) || password.length() < 6 || password.length() > 20) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD_LENGHT);
        }

        if (password.indexOf(" ") > -1) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public static void notNull(String str) throws ServiceException {
        if(StringUtils.isEmpty(str)){
            throw new ServiceException(ErrorCode.PARAM_ERROR);
        }
    }
}
