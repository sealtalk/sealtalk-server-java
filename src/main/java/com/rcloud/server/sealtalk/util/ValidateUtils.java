package com.rcloud.server.sealtalk.util;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 校验util
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ValidateUtils {


    private static final int FRIEND_REQUEST_MESSAGE_MIN_LENGTH = 0;

    private static final int FRIEND_REQUEST_MESSAGE_MAX_LENGTH = 64;

    private static final int FRIEND_DISPLAY_NAME_MIN_LENGTH = 1;

    private static final int FRIEND_DISPLAY_NAME_MAX_LENGTH = 32;

    //TODO
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
        if (StringUtils.isEmpty(str)) {
            throw new ServiceException(ErrorCode.PARAM_ERROR);
        }
    }

    public static void checkURL(String portraitUri) throws ServiceException {

        if (!RegexUtils.checkURL(portraitUri)) {
            throw new ServiceException(ErrorCode.INVALID_PORTRAITURI_FORMAT);
        }
    }

    public static void checkPortraitUri(String portraitUri) throws ServiceException {
        if (StringUtils.isEmpty(portraitUri) || portraitUri.length() < 12 || portraitUri.length() > 256) {
            throw new ServiceException(ErrorCode.INVALID_PORTRAITURI_LENGTH);
        }


    }

    public static void checkPokeStatus(Integer pokeStatus) throws ServiceException {
        ImmutableList<Integer> list = ImmutableList.of(0, 1);
        if (!list.contains(pokeStatus)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }
    }

    public static void checkPrivacy(Integer phoneVerify, Integer stSearchVerify, Integer friVerify, Integer groupVerify) throws ServiceException {
        if (phoneVerify == null && stSearchVerify == null && friVerify == null && groupVerify == null) {
            throw new ServiceException(ErrorCode.EMPTY_PARAMETER);
        }
        ImmutableList<Integer> list = ImmutableList.of(0, 1);
        if (!list.contains(phoneVerify) || !list.contains(stSearchVerify) || !list.contains(friVerify) || !list.contains(groupVerify)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

    }

    public static void checkGender(String gender) throws ServiceException {
        if (!"male".equals(gender) && !"female".equals(gender)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }


    }

    public static void checkStAccount(String stAccount) throws ServiceException {
        if (StringUtils.isEmpty(stAccount) || stAccount.length() < 6 || stAccount.length() > 20) {
            throw new ServiceException(ErrorCode.EMPTY_STACCOUNT_LENGHT_ERROR);
        }

        String regex = "^[a-zA-Z][a-zA-Z0-9_-]*$";
        if (!Pattern.matches(regex, stAccount)) {
            throw new ServiceException(ErrorCode.EMPTY_STACCOUNT_ERROR);
        }
    }

    public static void checkInviteMessage(String message) throws ServiceException {
        message = MiscUtils.xss(message,FRIEND_REQUEST_MESSAGE_MAX_LENGTH);

        if(StringUtils.isEmpty(message) || message.length()<FRIEND_REQUEST_MESSAGE_MIN_LENGTH || message.length()>FRIEND_REQUEST_MESSAGE_MAX_LENGTH){
            throw new ServiceException(ErrorCode.INVALID_INVITE_MESSAGE_LENGTH);
        }

    }
}
