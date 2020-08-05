package com.rcloud.server.sealtalk.spi.verifycode.impl;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.spi.verifycode.BaseVerifyCodeAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class YunPianVerifyCodeAuthentication extends BaseVerifyCodeAuthentication {

    @Override
    protected void serviceValidate(VerificationCodes verificationCodes, String code) throws ServiceException{
        if(verificationCodes.getSessionId().equals(code)){
            return ;
        }else {
            throw new ServiceException(ErrorCode.INVALID_VERIFY_CODE);
        }
    }
}
