package com.rcloud.server.sealtalk.spi.verifycode;

import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.spi.verifycode.impl.DefaultVerifyCodeAuthentication;
import com.rcloud.server.sealtalk.spi.verifycode.impl.YunPianVerifyCodeAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class VerifyCodeAuthenticationFactory {


    private static ConcurrentHashMap<SmsServiceType, VerifyCodeAuthentication> verifyCodeAuthenticationMap = new ConcurrentHashMap<>();

    @Resource
    private DefaultVerifyCodeAuthentication defaultVerifyCodeAuthentication;
    @Resource
    private YunPianVerifyCodeAuthentication yunPianVerifyCodeAuthentication;

    @PostConstruct
    public void postConstruct() {
        verifyCodeAuthenticationMap.put(SmsServiceType.RONGCLOUD, defaultVerifyCodeAuthentication);
        verifyCodeAuthenticationMap.put(SmsServiceType.YUNPIAN, yunPianVerifyCodeAuthentication);
    }

    public static VerifyCodeAuthentication getVerifyCodeAuthentication(SmsServiceType smsServiceType) throws ServiceException {
        return verifyCodeAuthenticationMap.get(smsServiceType);
    }
}
