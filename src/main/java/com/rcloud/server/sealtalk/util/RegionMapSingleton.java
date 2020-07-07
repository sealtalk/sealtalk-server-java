package com.rcloud.server.sealtalk.util;

import java.util.HashMap;

/**
 * @Author, xiuwei.nie
 * @Date, 2020/7/7
 * @Description,
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class RegionMapSingleton {

    private static RegionMapSingleton instance = null;
    private static HashMap<String, String> regionMap = new HashMap<>();

    private RegionMapSingleton() {}

    public static RegionMapSingleton getInstance() {
        if (instance == null) {
            instance = new RegionMapSingleton();
            init();
        }
        return instance;
    }

    public HashMap<String, String> getRegionMap(){
        return regionMap;
    }

    private static void init() {
        regionMap.put("86", "zh_cn"); // 中国大陆
        regionMap.put("852", "zh_tw"); // 中国香港
        regionMap.put("853", "zh_tw"); // 中国澳门
        regionMap.put("886", "zh_tw"); // 中国台湾
        regionMap.put("81", "ja"); // 日本
        regionMap.put("82", "ko"); // 韩国
        regionMap.put("other", "en");
    }
}
