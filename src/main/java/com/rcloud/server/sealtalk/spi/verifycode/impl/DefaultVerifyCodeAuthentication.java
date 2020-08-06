package com.rcloud.server.sealtalk.spi.verifycode.impl;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.spi.verifycode.BaseVerifyCodeAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class DefaultVerifyCodeAuthentication extends BaseVerifyCodeAuthentication {

    @Resource
    private SealtalkConfig sealtalkConfig;

    @Override
    public SmsServiceType getIdentification() {
        return SmsServiceType.RONGCLOUD;
    }

    /**
     * 调用融云sdk 校验短信验证码
     * @param verificationCodes
     * @param code
     */
    @Override
    protected void serviceValidate(VerificationCodes verificationCodes,String code) {

        if(StringUtils.isEmpty(sealtalkConfig.getRongcloudSmsRegisterTemplateId())&& Constants.DEFAULT_VERIFY_CODE.equals(code)){
            return;
        }
        //TODO，调用融云验证码校验接口
        return;
    }


}
