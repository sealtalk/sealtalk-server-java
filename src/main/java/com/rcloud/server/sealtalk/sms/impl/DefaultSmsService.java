package com.rcloud.server.sealtalk.sms.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.sms.SmsService;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import io.rong.RongCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 融云sms获取短信验证码
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class DefaultSmsService implements SmsService {
    @Resource
    protected SealtalkConfig sealtalkConfig;

    @Override
    public SmsServiceType getIdentifier() {
        return SmsServiceType.RONGCLOUD;
    }

    /**
     * 发送验证码
     *
     * @param region 地区，如中国大陆地区 region=86
     * @param phone  手机号
     * @return 返回验证码对应的唯一标示
     * @throws ServiceException
     */
    @Override
    public String sendVerificationCode(String region, String phone) throws ServiceException {

        if (!"".equals(sealtalkConfig.getRongcloudSmsRegisterTemplateId())) {
            String result = sendToSms(region, phone);
            try {
                JsonNode jsonNode = JacksonUtil.getJsonNode(result);
                int code = jsonNode.get("code").asInt();
                String sessionId = jsonNode.get("sessionId").asText();
                if (Constants.HTTP_SUCCESS_CODE != code) {
                    log.error("RongCloud Server API Error. Code:[{}]", code);
                    throw new ServiceException(ErrorCode.SEND_SMS_ERROR,
                            "RongCloud Server API Error Code: " + code);
                }
                return sessionId;
            } catch (Exception e) {
                log.error("Send sms result to json error,message:"+e.getMessage(), e);
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        } else {
            log.error("Rongcloud Sms Register TemplateId is null");
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 调用融云sdk 发送短信
     */
    private String sendToSms(String region, String phone) {
        RongCloud rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(),
                sealtalkConfig.getRongcloudAppSecret());
        //TODO send sms

        return "";
    }
}
