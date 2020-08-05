package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.VerificationCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.UUID;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VerificationCodesServiceTest {

    @Autowired
    private VerificationCodesService verificationCodesService;

    @Test
    public void queryOne() {

        String region = "86";
        String phone = "18810183283";

        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);

        System.out.println(verificationCodes);
        Assert.notNull(verificationCodes, "verificationCodes is null");
    }

    @Test
    public void testQueryOne() {
        String token = "57858a44-2707-482d-9ded-c5e12c07166c";
        VerificationCodes verificationCodes = verificationCodesService.queryOne(token);
        System.out.println(verificationCodes);
        Assert.notNull(verificationCodes, "verificationCodes is null");
    }

    @Test
    public void insert() {

        String region = "86";
        String phone = "18810183566";
        String sessionId = "";

        VerificationCodes v = new VerificationCodes();
        v.setRegion(region);
        v.setPhone(phone);
        v.setSessionId(sessionId);
        v.setCreatedAt(new Date());
        v.setUpdatedAt(new Date());
        v.setToken("");
        verificationCodesService.insert(v);
    }

    @Test
    public void update() {
        String region = "86";
        String phone = "18810183888";

        VerificationCodes v = verificationCodesService.queryOne(region,phone);
        v.setSessionId("test123");
        v.setUpdatedAt(new Date());
        verificationCodesService.update(v);

    }
}