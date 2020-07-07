package com.rcloud.server.sealtalk.util;

import java.util.Random;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class RandomUtil {

    private static Random random = new Random();

    public static int randomBetween(Integer min, Integer max){
        return random.nextInt() * (max - min + 1) + min;
    }
}
