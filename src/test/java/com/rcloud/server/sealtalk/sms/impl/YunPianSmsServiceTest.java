package com.rcloud.server.sealtalk.sms.impl;

import com.rcloud.server.sealtalk.exception.ServiceException;
import com.yunpian.sdk.model.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.List;

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
    public void testSendVerificationCode() throws ServiceException {
        String region="86";
        String phone = "18810183283";

        String code = yunPianSmsService.sendVerificationCode(region,phone);
        System.out.println(code);
    }



    @Test
    public void testGetTplIdByList() throws ServiceException {

        String region="86";
        String tplContent = yunPianSmsService.getTplIdByList(region);
        System.out.println(tplContent);
    }

    @Test
    public void testGetSmsTplList() throws ServiceException {

        List<Template> templates = yunPianSmsService.getSmsTplList();
        Assert.notEmpty(templates,"yunPianSmsService getSmsTplList is null ");
        System.out.println(templates);

    }

    @Test
    public void testGetRemoteSmSTplList() throws ServiceException {

        List<Template> templateList = yunPianSmsService.getRemoteSmSTplList();
        Assert.notEmpty(templateList,"获取云片模版列表为空");

        System.out.println(templateList);

    }
}