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

    public final static String SMS_YUNPIAN_URL = "sms.yunpian.com";
    public final static String US_YUNPIAN_URL = "us.yunpian.com";
    public final static String YUNPIAN_TPL_URI = "/v2/tpl/get.json";

    public static final String SERVER_API_PARAMS = "serverApiParams";
    public static final String ENV_DEV = "dev";
    public static final String VERIFICATION_TOKEN_KEY = "verification_token";

    public static final int HTTP_SUCCESS_CODE = 200;

    public static final String DEFAULT_VERIFY_CODE = "9999";


    public static final String CONTACT_OPERATION_ACCEPT_RESPONSE = "AcceptResponse";

    public static final String CONTACT_OPERATION_REQUEST = "Request";


    public static final String CONVERSATION_TYPE_PRIVATE = "PRIVATE";
    public static final String CONVERSATION_TYPE_GROUP = "GROUP";

    public static final int MAX_USER_GROUP_OWN_COUNT = 500;

    public static final String URL_GET_RONGCLOUD_IMG_CODE = "http://api.sms.ronghub.com/getImgCode.json?appKey=";

    public static final String SEPARATOR_ESCAPE = "\\|";
    public static final String SEPARATOR_NO = "|";


    //GrpApplyMessage默认fromUserId标示
    public static final String GrpApplyMessage_fromUserId = "__group_apply__";

    public static final Integer CODE_OK = 200;
    public static final Integer KICK_STATUS_SELF = 0;
    public static final Integer KICK_STATUS_MANAGER = 1;





}
