package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.sms.impl.YunPianSmsService;
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
public class N3dTest {
    @Test
    public void name() throws ServiceException {
        // id=101->U6BnNu73w
        System.out.println(N3d.encode(101));
        //id=110->3tge7fGra
        System.out.println(N3d.encode(110));
        //id=110->aAKZwl3Yn
        System.out.println(N3d.encode(111));
        //id=110->
        System.out.println(N3d.encode(113));
        /**
         * U6BnNu73w
         * 3tge7fGra
         * aAKZwl3Yn
         * mjfl43NCB
         */

        System.out.println(N3d.decode("pLwiBIrW9"));
    }
}