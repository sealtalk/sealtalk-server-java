package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class N3dTest {
    @Test
    public void name() throws ServiceException {

        System.out.println("groupId H6SzV9OXf: "+N3d.decode("Vgu7CtfIs"));
        System.out.println("groupId H6SzV9OXf: "+N3d.decode("41PARF60O"));
        System.out.println("groupId H6SzV9OXf: "+N3d.encode(28));
        System.out.println("groupId H6SzV9OXf: "+N3d.encode(111));


    }
}