package com.rcloud.server.sealtalk.constant;

public enum ErrorCode {

    //common error
    COOKIE_ERROR(404, "Invalid cookie value!",404),
    SEND_SMS_ERROR(401, "Send sms error.",401),
    SERVER_ERROR(500, "Server error.",500),
    PARAM_ERROR(400, "缺少参数，请检查。",400),
    YUN_PIAN_SMS_ERROR(3004, "Too many times sent",3004),
    TPL_FAILED_ERROR(3001, "Failed to get YunPian template",3001),
    REQUEST_ERROR(400, "错误的请求",400),
    ILLEGAL_PARAMETER(400,"Illegal parameter .",400),
    EMPTY_PARAMETER(400,"Parameter is empty.",400),
    PARAMETER_ERROR(400,"Parameter error.",400),

    EMPTY_STACCOUNT_LENGHT_ERROR(400,"Incorrect parameter length.",400),
    EMPTY_STACCOUNT_ERROR(400,"Not letter beginning or invalid symbol.",400),
    EMPTY_STACCOUNT_EXIST(1000,"该热聊号已存在",200),

    //send_code
    LIMIT_ERROR(5000, "Throttle limit exceeded.",200),
    INVALID_REGION_PHONE(400, "Invalid region and phone number.",400),

    //invoke rongcloud server error
    INVOKE_RONG_CLOUD_SERVER_ERROR(2000,"RongCloud Server API Error: ",2000),


    //verify_code error
    UNKOWN_PHONE_NUMBER(404, "Unknown phone number.",404),
    VERIFY_CODE_EXPIRED(2000, "Verification code expired.",200),
    INVALID_VERIFY_CODE(1000, "Invalid verification code.",200),

    //register error
    INVALID_PASSWORD(400, "Password must have no space.",400),
    INVALID_NICKNAME_LENGTH(400, "Length of nickname invalid.",400),
    INVALID_PASSWORD_LENGHT(400, "Length of password invalid.",400),
    INVALID_VERIFICATION_TOKEN(400, "Invalid verification_token.",400),
    UNKNOWN_VERIFICATION_TOKEN(404, "Unknown verification_token.",404),
    PHONE_ALREADY_REGIESTED(400, "该账号已经注册.",400),

    //login error
    USER_NOT_EXIST(1000, "该账号不存在.",200),
    USER_PASSWORD_WRONG(1001, "密码错误.",200),
    USER_PASSWORD_WRONG_2(1000, "旧密码错误.",200),

    //user error
    INVALID_PORTRAITURI_FORMAT(400,"Invalid portraitUri format.",400),
    INVALID_PORTRAITURI_LENGTH(400,"Invalid portraitUri length.",400),
    FRIEND_USER_NOT_EXIST(400,"friendId is not an available userId.",400),
    UNKNOW_USER(404,"Unknown user.",404),
    INVALID_REGION_LIST(1000,"Invalid region list.",1000),
    INVALID_TIMESTAMP_VERSION(400,"Version parameter is not integer.",400),

    //misc error
    UNSUPPORTED_CONVERSATION_TYPE(403,"Unsupported conversation type.",403),
    NOT_YOUR_FRIEND(403,"User {} is not your friend.",403),
    NOT_YOUR_GROUP(403,"Your are not member of Group {} .",403),



    //friendShip error
    INVALID_INVITE_MESSAGE_LENGTH(400,"Length of friend request message is out of limit.",400),
    UNKNOW_FRIEND_USER_OR_INVALID_STATUS(404,"Unknown friend user or invalid status.",404),
    NOT_FRIEND_USER(403,"Current user is not friend of user",403),
    ALREADY_YOUR_FRIEND(400, "User + friendId  is already your friend.",400),


    //group error
    INVALID_GROUP_NAME_LENGTH(400,"Length of group name is out of limit.",400),
    INVALID_GROUP_MEMNBER_COUNT(400,"Group's member count should be greater than 1 at least.",400),
    INVALID_GROUP_MEMNBER_MAX_COUNT(400,"Group's member count is out of max group member count limit.",400),
    INVALID_USER_GROUP_COUNT_LIMIT(1000,"Current user's group count is out of max user group count limit.",200),
    INVALID_PARAM_CREATOR(400,"Creator is not in memeber list.",400),
    GROUP_LIMIT_ERROR(20002,"Creator is not in memeber list.",20002),
    GROUP_UNKNOWN_ERROR(404,"Unknown group.",404),
    NOT_GROUP_MANAGER(400,"Current user is not group manager.",400),
    NOT_GROUP_MEMBER(404,"Not a group member",404),
    NOT_GROUP_OWNER(400,"Not a group owner .",400),
    NO_PERMISSION(20001,"No permission",20001),
    NOT_GROUP_MEMBER_2(403,"Only group member can get group member info.",403),
    INVALID_DISPLAY_NAME_LENGTH(400,"Length of display name invalid.",400),
    INVALID_GROUP_PORTRAITURI_LENGTH(400,"Length of portraitUri invalid.",400),
    GROUP_OR_CREATOR_UNKNOW(400,"Unknown group or not creator.",400),
    NO_GROUP_BULLETIN(402,"No group bulletin.",402),
    INVALID_GROUP_BULLETIN(400,"Length of bulletin invalid.",400),
    ALREADY_EXISTS_GROUP(405,"Group already exists.",405),
    TRANSFER_TO_CREATOR_ERROR(403,"Can not transfer creator role to yourself.",403),
    INVALID_GROUPID_USERID(403,"Invalid groupId or userId.",403),
    NOT_GROUP_CREATOR(400,"Current user is not group creator.",400),
    QUIT_IM_SERVER_ERROR(500,"Quit failed on IM server.",500),
    NOT_GROUP_MEMBER_3(403,"Current user is not group member.",403),
    NOT_GROUP_MANAGER_3(403,"Current user is not group manager.",403),
    CAN_NOT_KICK_YOURSELF(400,"Can not kick yourself.",400),
    CAN_NOT_KICK_CREATOR(405,"Can not kick the host.",405),
    GROUP_MEMBER_EMPTY(500,"Group member should not be empty, please check your database.",500),
    EMPTY_MEMBERID(400,"Empty memberId.",400),
    CANT_NOT_KICK_NONE_MEMBER(400,"Can not kick none-member from the group.",400),
    NOT_IN_MEMBER(403,"Not in the group.",403),
    NO_PERMISSION_SET_MANAGER(401,"No permission to set up an manager.",401),
    CAN_NOT_SET_CREATOR(403,"Cannot set the group creator.",403),
    NO_GROUP(20006,"No Group",200),
    IN_PROTECTED_GROUP(20004,"Protected",200),
    COPIED_GROUP(20005,"Copied",200),
    MEMBER_LIMIT(20007,"Member Limit",200),
    NOT_FOUND(20003,"Not found",200),


    //yunpian error 云片服务错误，错误码3000 开头
    YP_SERVER_FAILD(3000, "YunPian server error",3000),
    YP_GET_TEMPLATE_FAILD(3001, "Failed to get YunPian template",3001),
    YP_TEMPLATE_EMPTY(3002, "YunPian SMS template is empty",3002),
    YP_SEND_VERIFYCODER_FAILD(3003, "Send YunPian SMS code failed",3003),
    YP_SNED_TIMES_VIOLATION(3004, "Too many times sent",3004);

    private int errorCode;
    private String errorMessage;
    private int httpStatusCode;

    ErrorCode(int errorCode, String errorMessage,int httpStatusCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatusCode = httpStatusCode;

    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
