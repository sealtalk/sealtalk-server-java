package com.rcloud.server.sealtalk.spi.verifycode;

import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.exception.ServiceException;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 验证码认证
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface VerifyCodeAuthentication {


    SmsServiceType getIdentifier();

    /**
     * 短信验证码校验
     * @param verificationCodes
     * @param code
     * @param env  开发环境/正式环境
     * @return
     * @throws ServiceException
     */
    boolean validate(VerificationCodes verificationCodes,String code,String env) throws ServiceException;
}
