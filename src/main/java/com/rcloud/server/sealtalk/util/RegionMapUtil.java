package com.rcloud.server.sealtalk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author, xiuwei.nie
 * @Date, 2020/7/7
 * @Description,
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class RegionMapUtil {
    private static ConcurrentHashMap<String, String> regionMap = new ConcurrentHashMap<>();

    static {
        //地区对应关系语言模版
        regionMap.put("86", "zh_cn"); // 中国大陆
        regionMap.put("852", "zh_tw"); // 中国香港
        regionMap.put("853", "zh_tw"); // 中国澳门
        regionMap.put("886", "zh_tw"); // 中国台湾
        regionMap.put("81", "ja"); // 日本
        regionMap.put("82", "ko"); // 韩国
        regionMap.put("other", "en");
    }

    public static Map<String,String> getRegionMap(){
        return regionMap;
    }

    public static String getLangByRegion(String region){
        return regionMap.get(region);
    }


}
