package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
public class MiscUtilsTest {


    @Test
    public void testMerge() {

        String content = "【TEST】您的验证码是#code#。如非本人操作，请忽略本短信，test，test";
        String key = "#code#";
        String code = "123456";
        String text = MiscUtils.merge(content, key, code);
        System.out.println(text);

        Map<String, String> map = new HashMap<>();
        map.put("abc", 123 + "");
        Users u = new Users();
        log.info("test {},{},{},{}", 3, key, map, u);
    }

    @Test
    public void testDate() {
        Date now = new Date();
        DateTime dateTime = new DateTime(new Date());
        dateTime = dateTime.minusHours(1);
        Date limitDate = dateTime.toDate();
        System.out.println(limitDate);
        System.out.println(now);
        System.out.println(limitDate.before(now));
    }


    @Test
    public void name2() throws ServiceException {

        ClassA classA = new ClassA();
        String[] region = {"86", "87", "88"};
        classA.setRegion(region);
        classA.setPhone("18810183283");
        classA.setPassword("222222");

        System.out.println(JacksonUtil.toJson(classA));


    }

    @Test
    public void testHash() {

        String password = "yu@1876abc$#";
        int salt = RandomUtil.randomBetween(1000, 9999);

        salt = 7261;
        System.out.println("salt:" + salt);
        String hashStr = MiscUtils.hash(password, salt);
        System.out.println("hashStr:" + hashStr);
    }
}