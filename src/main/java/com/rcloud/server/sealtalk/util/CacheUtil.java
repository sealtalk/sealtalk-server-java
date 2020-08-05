package com.rcloud.server.sealtalk.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class CacheUtil {

    private static Cache<String,String> cache = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .expireAfterWrite(3600000, TimeUnit.MILLISECONDS)
            .build();

    public static String get(String key){
        return cache.getIfPresent(key);
    }

    public static void set(String key,String value){
        cache.put(key,value);
    }

    public static void delete(String key){
        cache.invalidate(key);
    }



}


