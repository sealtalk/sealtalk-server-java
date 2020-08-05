package com.rcloud.server.sealtalk.spi.verifycode;

import com.rcloud.server.sealtalk.SealtalkServerApplication;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.spi.verifycode.VerifyCodeAuthentication;

import javax.annotation.Resource;
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
    public boolean validate(VerificationCodes verificationCodes,String code,String env) throws ServiceException{
        commonValidate(verificationCodes,code,env);
        serviceValidate(verificationCodes,code);
        return true;
    }

    protected abstract void serviceValidate(VerificationCodes verificationCodes,String code) throws ServiceException;


    protected void commonValidate(VerificationCodes verificationCodes,String code,String env) throws ServiceException{

        if (verificationCodes == null) {
            throw new ServiceException(ErrorCode.UNKOWN_PHONE_NUMBER);
        } else {
            Calendar updateAtCal = Calendar.getInstance();
            Date updateAt = verificationCodes.getUpdatedAt();
            updateAtCal.setTime(updateAt);
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, -2);
            if (now.after(updateAtCal)) {
                throw new ServiceException(ErrorCode.VERIFY_CODE_EXPIRED);
            }
            if (Constants.ENV_DEV.equals(env) && Constants.DEFAULT_VERIFY_CODE.equals(code)) {
                return ;
            }
        }
    }


}
