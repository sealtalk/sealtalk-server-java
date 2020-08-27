package com.rcloud.server.sealtalk.constant;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class Constants {

    public static final int FRIEND_REQUEST_MESSAGE_MIN_LENGTH = 0;
    public static final int FRIEND_REQUEST_MESSAGE_MAX_LENGTH = 64;

    public final static String REGION_NUM = "86";
    public final static String REGION_NAME = "zh-CN";
    public static final String STRING_ADD = "+";

    public static final String ENV_DEV = "dev";
    public static final String VERIFICATION_TOKEN_KEY = "verification_token";

    public static final int HTTP_SUCCESS_CODE = 200;

    public static final String DEFAULT_VERIFY_CODE = "9999";


    public static final String CONTACT_OPERATION_ACCEPT_RESPONSE = "AcceptResponse";

    public static final String CONTACT_OPERATION_REQUEST = "Request";


    public static final String CONVERSATION_TYPE_PRIVATE = "PRIVATE";
    public static final String CONVERSATION_TYPE_GROUP = "GROUP";

    public static final int MAX_USER_GROUP_OWN_COUNT = 500;

    //TODO海外版
    public static final String URL_GET_RONGCLOUD_IMG_CODE = "http://api.sms.ronghub.com/getImgCode.json?appKey=";

    public static final String SEPARATOR_ESCAPE = "\\|";
    public static final String SEPARATOR_NO = "|";


    //群组申请消息 GrpApplyMessage默认fromUserId标示
    public static final String GrpApplyMessage_fromUserId = "__group_apply__";

    //群组通知消息 GrpApplyMessage默认fromUserId标示
    public static final String GroupNotificationMessage_fromUserId = "__system__";


    public static final Integer CODE_OK = 200;
    public static final String DATE_FORMATR_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

}
