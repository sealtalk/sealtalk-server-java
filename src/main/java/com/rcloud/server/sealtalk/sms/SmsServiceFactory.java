package com.rcloud.server.sealtalk.sms;

import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class SmsServiceFactory {

    private static ConcurrentHashMap<SmsServiceType, SmsService> smsServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {

        Map<String, SmsService> serviceMap =  applicationContext.getBeansOfType(SmsService.class);
        if(serviceMap!=null){
            Set<Map.Entry<String,SmsService>> smsServiceSet = serviceMap.entrySet();
            Iterator<Map.Entry<String,SmsService>> iterator = smsServiceSet.iterator();

            while(iterator.hasNext()){
                Map.Entry<String,SmsService> smsServiceEntry = iterator.next();
                SmsService smsService = smsServiceEntry.getValue();
                smsServiceMap.put(smsService.getIdentifier(), smsService);
            }
        }

    }

    public static SmsService getSmsService(SmsServiceType smsServiceType) throws ServiceException {
        return smsServiceMap.get(smsServiceType);
    }
}
