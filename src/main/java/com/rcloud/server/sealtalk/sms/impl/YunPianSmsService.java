package com.rcloud.server.sealtalk.sms.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.HttpStatusCode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
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


    private static final int VERIFICATION_CODE_MIN = 100000;
    private static final int VERIFICATION_CODE_MAX = 999999;
    private static final String TEMPLATE_VAL_KEY = "#code#";//短信模版key
    private static final String CHINESE_REGION = "86";//中国区

    @Resource
    private SealtalkConfig sealtalkConfig;
    private String apiKey;
    private YunpianClient yunpianClient;

    @Value("classpath:yunpian.properties")
    private org.springframework.core.io.Resource yunpianResource;

    //短信模版缓存
    Cache<String, List<Template>> templateCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();


    static {
        //地区，对应短信模版id TODO
        regionTemplateMap.put("86", "3910922");
        regionTemplateMap.put("852", "3910922");
        regionTemplateMap.put("853", "3910922");
        regionTemplateMap.put("886", "3910922");
        regionTemplateMap.put("81", "3910922");
        regionTemplateMap.put("82", "3910922");
        regionTemplateMap.put("other", "3910922");
    }


    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isEmpty(sealtalkConfig.getYunpianApiKey())) {
            log.error("yunpian apikey is null");
            throw new RuntimeException("yunpian api key is null");
        }
        this.apiKey = sealtalkConfig.getYunpianApiKey();
        try {
            yunpianClient = new YunpianClient(apiKey, yunpianResource.getInputStream()).init();
        } catch (IOException e) {
            log.error("yunpian client init error:" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public SmsServiceType getIdentifier() {
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
     *
     * @param region  86
     * @return
     * @throws ServiceException
     */
    public String getTplIdByList(String region) throws ServiceException {
        Long tplId = Long.valueOf(regionTemplateMap.get(region));

        List<Template> templates = getSmsTplList();
        if (templates != null) {
            for (Template template : templates) {
                if (template.getTpl_id() != null && tplId.equals(template.getTpl_id())) {
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

    /**
     * 调用云片接口获取短信模版
     *
     * @return
     * @throws ServiceException
     */
    public List<Template> getRemoteSmSTplList() throws ServiceException {

        List<Template> list = null;
        Map<String, String> param = yunpianClient.newParam(1);
        param.put(YunpianClient.APIKEY, sealtalkConfig.getYunpianApiKey());

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
        throw new ServiceException(errorCode, result.getMsg(), HttpStatusCode.CODE_200.getCode());
    }


    @PreDestroy
    public void preDestroy() {
        yunpianClient.close();
    }


}
