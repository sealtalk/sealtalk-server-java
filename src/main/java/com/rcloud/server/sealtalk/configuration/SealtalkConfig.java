package com.rcloud.server.sealtalk.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author;
 * @Date;
 * @Description;
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
@Component
public class SealtalkConfig {

    @Value("${sealtalk-config.auth_cookie_name}")
    private String authCookieName;
    @Value("${sealtalk-config.auth_cookie_key}")
    private String authCookieKey;
    @Value("${sealtalk-config.nickname_cookie_name}")
    private String nicknameCookieName;
    @Value("${sealtalk-config.auth_cookie_max_age}")
    private String authCookieMaxAge;
    @Value("${sealtalk-config.rongcloud_sms_register_template_id}")
    private String rongcloudSmsRegisterTemplateId;
    @Value("${sealtalk-config.rongcloud_app_key}")
    private String rongcloudAppKey;
    @Value("${sealtalk-config.rongcloud_app_secret}")
    private String rongcloudAppSecret;
    @Value("${sealtalk-config.qiniu_access_key}")
    private String qiniuAccessKey;
    @Value("${sealtalk-config.qiniu_secret_key}")
    private String qiniuSecretKey;
    @Value("${sealtalk-config.qiniu_bucket_name}")
    private String qiniuBucketName;
    @Value("${sealtalk-config.qiniu_bucket_domain}")
    private String qiniuBucketDomain;
    @Value("${sealtalk-config.yunpian_api_key}")
    private String yunpianApiKey;
//    @Value("${sealtalk-config.yunpian_sms_host}")
//    private String yunpianSmsHost;
//    @Value("${sealtalk-config.yunpian_internal_sms_host}")
//    private String yunpianInternalSmsHost;
//    @Value("${sealtalk-config.yunpian_send_sms_uri}")
//    private String yunpianSendSmsUri;
//    @Value("${sealtalk-config.yunpian_get_tpl_uri}")
    private String yunpianGetTplUri;
    @Value("${sealtalk-config.n3d_key}")
    private String n3dKey;
    @Value("${sealtalk-config.auth_cookie_domain}")
    private String authCookieDomain;
    @Value("${sealtalk-config.cors_hosts}")
    private String corsHosts;
    @Value("${sealtalk-config.yunpian_limited_time:1}")//限制小时
    private Integer yunpianLimitedTime;
    @Value("${sealtalk-config.yunpian_limited_count:20}")//限制次数
    private Integer yunpianLimitedCount;
    @Value("${sealtalk-config.exclude_url}")
    private String excludeUrl;
}
