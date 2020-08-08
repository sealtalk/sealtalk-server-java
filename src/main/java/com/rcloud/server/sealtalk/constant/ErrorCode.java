package com.rcloud.server.sealtalk.constant;

public enum ErrorCode {

    //common error
    COOKIE_ERROR(404, "Invalid cookie value!"),
    LIMIT_ERROR(5000, "Throttle limit exceeded."),
    SEND_SMS_ERROR(401, "Send sms error."),
    SERVER_ERROR(500, "Server error."),
    PARAM_ERROR(400, "缺少参数，请检查。"),
    YUN_PIAN_SMS_ERROR(3004, "Too many times sent"),
    TPL_FAILED_ERROR(3001, "Failed to get YunPian template"),
    REQUEST_ERROR(400, "错误的请求"),
    ILLEGAL_PARAMETER(400,"Illegal parameter ."),
    EMPTY_PARAMETER(400,"Parameter is empty."),

    EMPTY_STACCOUNT_LENGHT_ERROR(400,"Incorrect parameter length."),
    EMPTY_STACCOUNT_ERROR(400,"Not letter beginning or invalid symbol."),
    EMPTY_STACCOUNT_EXIST(1000,"st account exist"),

    //invoke rongcloud server error  TODO
    INVOKE_RONG_CLOUD_SERVER_ERROR(2000,"RongCloud Server API Error: "),


    //verify_code error
    UNKOWN_PHONE_NUMBER(404, "Unknown phone number."),
    VERIFY_CODE_EXPIRED(2000, "Verification code expired."),
    INVALID_VERIFY_CODE(1000, "Invalid verification code."),

    //register error
    INVALID_PASSWORD(400, "Password must have no space."),
    INVALID_NICKNAME_LENGTH(400, "Length of nickname invalid."),
    INVALID_PASSWORD_LENGHT(400, "Length of password invalid."),
    INVALID_VERIFICATION_TOKEN(400, "Invalid verification_token."),
    UNKNOWN_VERIFICATION_TOKEN(404, "Unknown verification_token."),
    PHONE_ALREADY_REGIESTED(404, "Phone number has already existed."),

    //login error
    USER_NOT_EXIST(1000, "Phone number not found."),
    USER_PASSWORD_WRONG(1001, "Wrong password."),
    USER_PASSWORD_WRONG_2(1000, "Wrong password."),

    //user error
    INVALID_PORTRAITURI_FORMAT(400,"Invalid portraitUri format."),
    INVALID_PORTRAITURI_LENGTH(400,"Invalid portraitUri length."),
    FRIEND_USER_NOT_EXIST(400,"friendId is not an available userId."),
    UNKNOW_USER(404,"Unknown user."),


    //yunpian error 云片服务错误，错误码3000 开头
    YP_SERVER_FAILD(3000, "YunPian server error"),
    YP_GET_TEMPLATE_FAILD(3001, "Failed to get YunPian template"),
    YP_TEMPLATE_EMPTY(3002, "YunPian SMS template is empty"),
    YP_SEND_VERIFYCODER_FAILD(3003, "Send YunPian SMS code failed"),
    YP_SNED_TIMES_VIOLATION(3004, "Too many times sent");

    private int errorCode;
    private String errorMessage;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
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
}
