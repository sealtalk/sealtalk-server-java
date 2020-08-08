package com.rcloud.server.sealtalk.sms;

import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: Sms服务
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */

public interface SmsService {


    SmsServiceType getIdentifier();

    /**
     * 发送短信验证码
     * @param region 地区，如中国大陆地区 region=86
     * @param phone 手机号
     * @return 验证码,如果是调用云片，返回的是验证码，如果是调用融云，返回的是sessionId(唯一标示)
     */
    String sendVerificationCode(String region, String phone) throws ServiceException;
}
