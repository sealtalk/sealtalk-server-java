//package com.rcloud.server.sealtalk.manager;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.rcloud.server.sealtalk.constant.Constants;
//import com.rcloud.server.sealtalk.constant.ErrorCode;
//import com.rcloud.server.sealtalk.exception.ServiceException;
//import com.rcloud.server.sealtalk.util.HttpClient;
//import com.rcloud.server.sealtalk.util.JacksonUtil;
//import com.rcloud.server.sealtalk.util.RandomUtil;
//import com.rcloud.server.sealtalk.util.RegionMapSingleton;
//import io.micrometer.core.instrument.util.IOUtils;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.concurrent.ConcurrentHashMap;
//import javax.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.joda.time.DateTime;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.StringUtils;
//
///**
// * @Author: xiuwei.nie
// * @Date: 2020/7/7
// * @Description:
// * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
// */
//@Service
//@Slf4j
//public class SmsManager extends BaseManager {
//
//    private static ConcurrentHashMap<String, Object> smsTempCache = new ConcurrentHashMap<>();
//
//    private static final int VERIFICATION_CODE_MIN = 100000;
//    private static final int VERIFICATION_CODE_MAX = 999999;
//
//    @Value("classpath:region.json")
//    private org.springframework.core.io.Resource regionResource;
//
//    @Resource
//    private HttpClient httpClient;
//
//    public String getSmsTplId(String region) throws ServiceException {
//        JsonNode jsonNode = getSmsTplList(region);
//        return getTplIdByList(jsonNode, region);
//    }
//
//    public JsonNode getSmsTplList(String region) throws ServiceException {
//        Object timeObj = smsTempCache.get("time");
//        Object listObj = smsTempCache.get("list");
//        long tplTime = 0L;
//        if (timeObj != null) {
//            if (timeObj instanceof Date) {
//                tplTime = ((Date) timeObj).getTime();
//            }
//        }
//        JsonNode tempNode = null;
//        if (listObj != null) {
//            tempNode = (JsonNode) listObj;
//        }
//        DateTime dateTime = new DateTime(new Date());
//        long tplInterval = dateTime.minusHours(1).toDate().getTime();
//        boolean isExpired = tplInterval > tplTime;
//        boolean isUpgrade = isExpired || tempNode != null;
//        if (!isUpgrade) {
//            return tempNode;
//        }
//        return getTempList(region);
//    }
//
//    /**
//     * 根据地区和模板列表 匹配模板, 获取模板 id
//     */
//    private String getTplIdByList(JsonNode arrNode, String region) {
//        HashMap<String, String> regionMap = RegionMapSingleton.getInstance().getRegionMap();
//        String regionTplLang = regionMap.get(region);
//        if (regionTplLang == null) {
//            regionTplLang = regionMap.get("other");
//        }
//        regionTplLang = regionTplLang.toLowerCase();
//        if (arrNode.isArray()) {
//            for (JsonNode jsonNode : arrNode) {
//                String lang = jsonNode.get("lang").asText();
//                if (regionTplLang.equals(lang)) {
//                    return jsonNode.get("tpl_id").asText();
//                }
//            }
//        }
//        return null;
//    }
//
//    private JsonNode getTempList(String region) throws ServiceException {
//        boolean isChinese = region.equals(Constants.REGION_NUM);
//        String smsHost = sealtalkConfig.getYunpianSmsHost();
//        smsHost = StringUtils.isEmpty(smsHost) ? Constants.SMS_YUNPIAN_URL : smsHost;
//        String internalSmsHost = sealtalkConfig.getYunpianInternalSmsHost();
//        internalSmsHost =
//            StringUtils.isEmpty(internalSmsHost) ? Constants.US_YUNPIAN_URL : internalSmsHost;
//        String path = sealtalkConfig.getYunpianGetTplUri();
//        path = StringUtils.isEmpty(path) ? Constants.YUNPIAN_TPL_URI : path;
//        String hostName = isChinese ? smsHost : internalSmsHost;
//        String url = hostName + path;
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("apikey", sealtalkConfig.getYunpianApiKey());
//        ResponseEntity<String> responseEntity = httpClient
//            .post(url, params, MediaType.APPLICATION_FORM_URLENCODED);
//        String body = responseEntity.getBody();
//        return jsonStrToJsonNode(body);
//    }
//
//    public void sendCode(String region, String phone) throws ServiceException {
//        String tplId = getSmsTplId(region);
//        int code = RandomUtil.randomBetween(VERIFICATION_CODE_MIN, VERIFICATION_CODE_MAX);
//        String mobile = Constants.STRING_ADD + region + phone;
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("apikey", sealtalkConfig.getYunpianApiKey());
//        params.add("mobile", mobile);
//        params.add("tpl_id", tplId);
//        params.add("tpl_value", "");
//    }
//
//    public JsonNode getRegionList() throws ServiceException {
//        try {
//            String regionData = IOUtils
//                .toString(regionResource.getInputStream(), StandardCharsets.UTF_8);
//            return jsonStrToJsonNode(regionData);
//        } catch (IOException e) {
//            log.error("regionData to jsonNode error.", e);
//            throw new ServiceException(ErrorCode.TPL_FAILED_ERROR);
//        }
//    }
//
//    private JsonNode jsonStrToJsonNode(String jsonStr) throws ServiceException {
//        return JacksonUtil.getJsonNode(jsonStr);
//    }
//}
