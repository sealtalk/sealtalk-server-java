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
//        System.out.println("101: "+N3d.encode(101));
//        System.out.println("110: "+N3d.encode(110));
//        System.out.println("111: "+N3d.encode(111));
//        System.out.println("113: "+N3d.encode(113));
//        System.out.println("groupId 10: "+N3d.encode(10));
//
//
//
//        System.out.println("groupId Sv7gGCckF: "+N3d.decode("Sv7gGCckF"));
//
//
//        System.out.println("groupId 5jLgxna0X: "+N3d.decode("5jLgxna0X"));
//        System.out.println("groupId xUw2IoeAV: "+N3d.decode("xUw2IoeAV"));
        System.out.println("groupId H6SzV9OXf: "+N3d.decode("H6SzV9OXf"));



//        uid Sv7gGCckF: 3
//        groupId 5jLgxna0X: 14



        System.out.println("groupId 10: "+N3d.encode(30));


        System.out.println("groupId ipJumrPq7: "+N3d.encode(621));
        System.out.println("groupId ipJumrPq7: "+N3d.encode(622));


    }
}