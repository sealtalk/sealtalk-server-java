package com.rcloud.server.sealtalk.spi.verifycode;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.exception.ServiceException;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public abstract class BaseVerifyCodeAuthentication implements VerifyCodeAuthentication {

    @Override
    public boolean validate(VerificationCodes verificationCodes, String code, String env) throws ServiceException {
        commonValidate(verificationCodes, code, env);
        serviceValidate(verificationCodes, code, env);
        return true;
    }

    protected abstract void serviceValidate(VerificationCodes verificationCodes, String code, String env) throws ServiceException;


    /**
     * 公共校验
     *
     * @param verificationCodes
     * @param code
     * @param env
     * @throws ServiceException
     */
    protected void commonValidate(VerificationCodes verificationCodes, String code, String env) throws ServiceException {

        if (verificationCodes == null) {
            //判断验证码记录是否存在
            throw new ServiceException(ErrorCode.UNKOWN_PHONE_NUMBER);
        } else {
            Calendar updateAtCal = Calendar.getInstance();
            Date updateAt = verificationCodes.getUpdatedAt();
            updateAtCal.setTime(updateAt);
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, -2);
            //判断验证码是否过期
            if (now.after(updateAtCal)) {
                throw new ServiceException(ErrorCode.VERIFY_CODE_EXPIRED);
            }

        }
    }


}
