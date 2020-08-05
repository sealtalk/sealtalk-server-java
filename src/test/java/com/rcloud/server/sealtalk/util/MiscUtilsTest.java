package com.rcloud.server.sealtalk.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class MiscUtilsTest {



    @Test
    public void testMerge(){

        String content = "【于建路】您的验证码是#code#。如非本人操作，请忽略本短信，test，test";
        String key = "#code#";
        String code = "123456";
        String text = MiscUtils.merge(content,key,code);
        System.out.println(text);
    }

}