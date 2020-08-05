package com.rcloud.server.sealtalk.sms.impl;

import com.rcloud.server.sealtalk.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 云片Sms服务测试
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class YunPianSmsServiceTest {


    @Autowired
    private YunPianSmsService yunPianSmsService;

    @Test
    public void sendVerificationCode() throws Exception {

        String region="86";
        String phone = "18810183283";
        try {
            String code = yunPianSmsService.sendVerificationCode(region,phone);
            System.out.println(code);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}