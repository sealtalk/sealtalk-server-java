package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.domain.Users;
import com.sun.media.jfxmediaimpl.HostUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
public class MiscUtilsTest {



    @Test
    public void testMerge(){

        String content = "【于建路】您的验证码是#code#。如非本人操作，请忽略本短信，test，test";
        String key = "#code#";
        String code = "123456";
        String text = MiscUtils.merge(content,key,code);
        System.out.println(text);

        Map<String,String> map = new HashMap<>();
        map.put("abc",123+"");

        Users u = new Users();

        log.error("test {},{},{},{}",3,key,map,u);
    }


}