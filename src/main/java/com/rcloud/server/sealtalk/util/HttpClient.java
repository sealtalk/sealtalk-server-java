package com.rcloud.server.sealtalk.util;

import javax.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Component
public class HttpClient {

    @Resource
    private RestTemplate restTemplate;

    public String post(String url, MultiValueMap<String, ?> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, ?>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(url, request, String.class);
        return entity.getBody();
    }

    public ResponseEntity<String> post(String url, MultiValueMap<String, ?> params,
        MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        HttpEntity<MultiValueMap<String, ?>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(url, request, String.class);
        return entity;
    }


    public String get(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
