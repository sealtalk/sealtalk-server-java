package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.constant.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class MiscUtils {

    /**
     * 地区添加前缀 "+"
     *
     * @param region 86
     * @return +86
     */
    public static String addRegionPrefix(String region) {
        if (!region.startsWith(Constants.STRING_ADD)) {
            region = Constants.STRING_ADD + region;
        }
        return region;
    }

    /**
     * 地区去掉前缀 "+"
     *
     * @param region +86
     * @return 86
     */
    public static String removeRegionPrefix(String region) {
        if (region.startsWith(Constants.STRING_ADD)) {
            region = region.substring(1);
        }
        return region;
    }

    public static String hash(String text,int salt) {
        if(StringUtils.isEmpty(text)){
            return null;
        }else{
            text = text+"|"+salt;
            return DigestUtils.sha1Hex(text);
        }
    }

    public static void main(String[] args) {
        String text="abcd123";
        int salt = 9988;


        //a2d46a186480138852a18cb1c8b2af530f3e5166
        System.out.println(hash(text,salt));
    }

    public static String merge(String content,String key,String code){
        content = content.replaceAll(key, code);
        return content;
    }

}
