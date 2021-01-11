package com.rcloud.server.sealtalk.sms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/23
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class SmsTemplateService {

    @Value("classpath:sms_template_map.json")
    private org.springframework.core.io.Resource smsTemplateResource;

    @Value("classpath:region_language_map.json")
    private org.springframework.core.io.Resource regionLanguageResource;

    private static ConcurrentHashMap<String, String> smsTemplateIdMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> regionLanguageMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void postConstruct() {
        try {

            String jsonData = IOUtils
                    .toString(smsTemplateResource.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            List<SmsTemplateVO> smsTemplateVOList = objectMapper.readValue(jsonData, new TypeReference<List<SmsTemplateVO>>() {
            });

            if (smsTemplateVOList != null) {
                for (SmsTemplateVO vo : smsTemplateVOList) {
                    if (!StringUtils.isEmpty(vo.getLang().trim())) {
                        smsTemplateIdMap.put(vo.getLang().trim(), vo.getId().trim());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("SmsTemplateService init exception:" + e.getMessage(), e);
        }


        try {

            String jsonData2 = IOUtils
                    .toString(regionLanguageResource.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            List<RegionLanguageVO> regionLanguageVOList = objectMapper.readValue(jsonData2, new TypeReference<List<RegionLanguageVO>>() {
            });

            if (regionLanguageVOList != null) {
                for (RegionLanguageVO vo : regionLanguageVOList) {
                    if (!StringUtils.isEmpty(vo.getLang().trim())) {
                        regionLanguageMap.put(vo.getRegion().trim(), vo.getLang().trim());
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("regionLanguageMap init exception:" + e.getMessage(), e);
        }
    }

    public Map<String, String> getSmsTemplateIdMap() {

        return smsTemplateIdMap;
    }

    public String getSmsTemplateIdByLang(String lang) {

        return smsTemplateIdMap.get(lang);
    }

    public static ConcurrentHashMap<String, String> getRegionLanguageMap() {
        return regionLanguageMap;
    }

    public String getLangByRegion(String region) {
        String lang = regionLanguageMap.get(region);
        if (StringUtils.isBlank(lang)) {
            //默认返回中文
            lang = "zh_cn";
        }
        return lang;
    }
}
