package com.rcloud.server.sealtalk.sms.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.sms.SmsService;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.RandomUtil;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import com.yunpian.sdk.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 云片Sms服务
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */

@Service
@Slf4j
public class YunPianSmsService implements SmsService {

    private static ConcurrentHashMap<String, String> regionTemplateMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Object> smsTempCache = new ConcurrentHashMap<>();

    private static final int VERIFICATION_CODE_MIN = 100000;
    private static final int VERIFICATION_CODE_MAX = 999999;
    private static final String TEMPLATE_VAL_KEY = "#code#";//短信模版key
    private static final String CHINESE_REGION = "86";//中国区

    @Resource
    private SealtalkConfig sealtalkConfig;
    private String apiKey;
    private YunpianClient yunpianClient;

    //短信模版缓存
    Cache<String, List<Template>> templateCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();


    static {
        regionTemplateMap.put("86", "zh_cn");
        regionTemplateMap.put("852", "zh_tw");
        regionTemplateMap.put("853", "zh_tw");
        regionTemplateMap.put("886", "zh_tw");
        regionTemplateMap.put("81", "ja");
        regionTemplateMap.put("82", "ko");
        regionTemplateMap.put("other", "en");
    }


    @PostConstruct
    public void postConstruct() throws ServiceException {
        if (StringUtils.isEmpty(sealtalkConfig.getYunpianApiKey())) {
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        this.apiKey = sealtalkConfig.getYunpianApiKey();
        yunpianClient = new YunpianClient(apiKey).init();
    }

    @Override
    public SmsServiceType getIdentification() {
        return SmsServiceType.YUNPIAN;
    }

    /**
     * 发送验证码
     *
     * @param region 地区，如中国大陆地区 region=86
     * @param phone  手机号
     * @return 验证码
     * @throws ServiceException
     */
    @Override
    public String sendVerificationCode(String region, String phone) throws ServiceException {

        String tplContent = getTplIdByList(region);
        if (StringUtils.isEmpty(tplContent)) {
            throw new ServiceException(ErrorCode.YP_TEMPLATE_EMPTY);
        }
        int code = RandomUtil.randomBetween(VERIFICATION_CODE_MIN, VERIFICATION_CODE_MAX);
        tplContent = tplContent.replaceAll(TEMPLATE_VAL_KEY, String.valueOf(code));
        //发送短信
        Map<String, String> param = yunpianClient.newParam(2);

        region = MiscUtils.addRegionPrefix(region);
        param.put(YunpianClient.APIKEY, apiKey);
        param.put(YunpianClient.MOBILE, region + phone);
        param.put(YunpianClient.TEXT, tplContent);
        try {
            Result<SmsSingleSend> r = yunpianClient.sms().single_send(param);

            if (r != null && r.isSucc()) {
                return String.valueOf(code);
            }
            processErrorResult(r);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), ErrorCode.YP_SERVER_FAILD, e);
        }
        return null;
    }


    /**
     * 根据地区和模板列表 匹配模板内容, 获取模板 内容
     */
    private String getTplIdByList(String region) throws ServiceException {
        String tplLang = regionTemplateMap.get(region);
        if (tplLang == null) {
            tplLang = regionTemplateMap.get("other");
        }
        tplLang = tplLang.toLowerCase();

        List<Template> templates = getSmsTplList();
        if (templates != null) {
            for (Template template : templates) {
                if (template.getLang() != null && tplLang.equalsIgnoreCase(template.getLang())) {
                    return template.getTpl_content();
                }
            }
        }
        return null;
    }


    /**
     * 获取云片短信模版
     *
     * @return
     * @throws ServiceException
     */
    public List<Template> getSmsTplList() throws ServiceException {

        try {
            return templateCache.get("templateCache", new Callable<List<Template>>() {
                @Override
                public List<Template> call() throws Exception {
                    return getRemoteSmSTplList();
                }
            });
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.YP_GET_TEMPLATE_FAILD, e.getMessage());
        }
    }

    private List<Template> getRemoteSmSTplList() throws ServiceException {

        List<Template> list = null;
        Map<String, String> param = yunpianClient.newParam(1);
        param.put("apikey", sealtalkConfig.getYunpianApiKey());

        try {
            Result<List<Template>> result = yunpianClient.tpl().get(param);
            if (result != null && result.isSucc()) {
                if (result.getData() == null && result.getData().size() < 1) {
                    throw new ServiceException(ErrorCode.YP_TEMPLATE_EMPTY);
                }
                list = result.getData();
            } else {
                processErrorResult(result);
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), ErrorCode.YP_GET_TEMPLATE_FAILD, e);
        }
        return list;
    }

    /**
     * 处理云片服务错误返回结果
     * 如果云片返回码是负数，需要取反后加100
     * 返回给应用前端返回码=3100+云片服务返回错误码
     *
     * @param result
     * @throws ServiceException
     */
    private void processErrorResult(Result<?> result) throws ServiceException {

        Integer errorCode = result.getCode();
        if (errorCode < 0) {
            errorCode = 100 - errorCode;
        }
        errorCode = 3100 + errorCode;
        throw new ServiceException(errorCode, result.getMsg());
    }


    @PreDestroy
    public void preDestroy() {
        yunpianClient.close();
    }


}
