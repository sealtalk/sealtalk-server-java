package com.rcloud.server.sealtalk.util;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 校验util
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ValidateUtils {


    public static final int NICKNAME_MIN_LENGTH = 1;

    public static final int NICKNAME_MAX_LENGTH = 32;

    public static final int FRIEND_REQUEST_MESSAGE_MIN_LENGTH = 0;

    public static final int FRIEND_REQUEST_MESSAGE_MAX_LENGTH = 64;

    public static final int FRIEND_DISPLAY_NAME_MIN_LENGTH = 1;

    public static final int FRIEND_DISPLAY_NAME_MAX_LENGTH = 32;

    public static final int GROUP_NAME_MIN_LENGTH = 2;

    public static final int GROUP_NAME_MAX_LENGTH = 32;

    public static final int GROUP_BULLETIN_MAX_LENGTH = 1024;

    public static final int PORTRAIT_URI_MIN_LENGTH = 12;

    public static final int PORTRAIT_URI_MAX_LENGTH = 256;

    public static final int GROUP_MEMBER_DISPLAY_NAME_MAX_LENGTH = 32;

    public static final int DEFAULT_MAX_GROUP_MEMBER_COUNT = 500;

    public static final int MAX_USER_GROUP_OWN_COUNT = 500;


    public static void notNull(Object o) throws ServiceException {
        if (o == null) {
            throw new ServiceException(ErrorCode.PARAM_ERROR);
        }
    }

    public static void notEmpty(String str) throws ServiceException {
        if (StringUtils.isEmpty(str)) {
            throw new ServiceException(ErrorCode.PARAM_ERROR);
        }
    }

    public static void notEmpty(String[] str) throws ServiceException {
        if (str == null || str.length == 0) {
            throw new ServiceException(ErrorCode.PARAM_ERROR);
        }
    }

    public static void checkCompletePhone(String completePhone) throws ServiceException {

        if (!RegexUtils.checkMobile(completePhone)) {
            throw new ServiceException(ErrorCode.INVALID_REGION_PHONE);
        }
    }

    public static void checkRegion(String region) throws ServiceException {
        if (!Constants.REGION_NUM.equals(region)) {
            throw new ServiceException(ErrorCode.INVALID_REGION_PHONE);
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

    public static void checkURLFormat(String portraitUri) throws ServiceException {

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

        if (phoneVerify != null && !list.contains(phoneVerify)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

        if (stSearchVerify != null && !list.contains(stSearchVerify)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

        if (friVerify != null && !list.contains(friVerify)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }
        if (groupVerify != null && !list.contains(groupVerify)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

    }

    public static void checkGender(String gender) throws ServiceException {
        if (!"male".equals(gender) && !"female".equals(gender)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR);
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
        if (StringUtils.isEmpty(message) || message.length() < FRIEND_REQUEST_MESSAGE_MIN_LENGTH || message.length() > FRIEND_REQUEST_MESSAGE_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.INVALID_INVITE_MESSAGE_LENGTH);
        }
    }

    public static void checkDisplayName(String displayName) throws ServiceException {

        if (StringUtils.isEmpty(displayName) || displayName.length() < FRIEND_DISPLAY_NAME_MIN_LENGTH || displayName.length() > FRIEND_DISPLAY_NAME_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.INVALID_INVITE_MESSAGE_LENGTH, "Length of displayName is out of limit.");
        }

    }

    public static boolean isLength(String str, int minLength, int maxLength) throws ServiceException {
        if (str == null || maxLength < minLength || maxLength <= 0 || minLength < 0) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

        return (str.length() >= minLength && str.length() <= maxLength);
    }

    public static void checkGroupName(String name) throws ServiceException {

        if (!isLength(name, GROUP_NAME_MIN_LENGTH, GROUP_NAME_MAX_LENGTH)) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_NAME_LENGTH);
        }

    }

    public static void checkMemberIds(String[] memberIds) throws ServiceException {
        if (memberIds == null || memberIds.length < 2) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_MEMNBER_COUNT);
        }

        if (memberIds.length > DEFAULT_MAX_GROUP_MEMBER_COUNT) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_MEMNBER_MAX_COUNT);
        }
    }


    /**
     * 判断 value是否在rangeList 区间范围内
     *
     * @param value
     * @param rangeList
     */
    public static <T> void valueOf(T value, List<T> rangeList) throws ServiceException {
        if (value == null || !rangeList.contains(value)) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }
    }

    public static void checkGroupDisplayName(String displayName) throws ServiceException {

        if (StringUtils.isEmpty(displayName) || displayName.length() > GROUP_MEMBER_DISPLAY_NAME_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.INVALID_DISPLAY_NAME_LENGTH);
        }


    }

    /**
     * 验证群头像地址
     *
     * @param portraitUri
     */
    public static void checkGroupPortraitUri(String portraitUri) throws ServiceException {
        if (!isLength(portraitUri, PORTRAIT_URI_MIN_LENGTH, PORTRAIT_URI_MAX_LENGTH)) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_PORTRAITURI_LENGTH);
        }
    }

    /**
     * 群公告校验
     *
     * @param bulletin
     */
    public static void checkGroupBulletion(String bulletin) throws ServiceException {

        if (bulletin == null || bulletin.length() > GROUP_BULLETIN_MAX_LENGTH) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_BULLETIN);
        }
    }

    public static void checkTimeStamp(String timeStamp) throws ServiceException {

        if(!RegexUtils.checkDigit(timeStamp)){
            throw new ServiceException(ErrorCode.INVALID_TIMESTAMP_VERSION);
        }
    }
}
