package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.util.N3d;
import io.rong.models.response.TokenResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/18
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultRongCloudClientTest {

    @Autowired
    private DefaultRongCloudClient defaultRongCloudClient;

    @Test
    public void register() throws ServiceException {

        String encodeId = N3d.encode(110);
        System.out.println(encodeId);

        String name="tom1";
//        String portrait="http://test.com/user/abc123.jpg";
        String portrait="";
        TokenResult tokenResult = defaultRongCloudClient.register(encodeId,name,portrait);

        System.out.println(tokenResult);
    }


}