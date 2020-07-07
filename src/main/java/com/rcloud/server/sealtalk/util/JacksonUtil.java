package com.rcloud.server.sealtalk.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class JacksonUtil {

    private static ObjectMapper mapper;

    public JacksonUtil(JsonInclude.Include include) {
        mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 获取ObjectMapper实例
     *
     * @param createNew 方式：true，新实例；false,存在的mapper实例
     */
    public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
        if (createNew) {
            return new ObjectMapper();
        } else if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    /**
     * 只输出非空属性
     */
    public static String toJsonNotNull(Object o) throws Exception {
        return nonNullMapper().toJson(o);
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     */
    public static String toJson(Object object) throws Exception {

        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 创建只输出非Null的属性到Json字符串的Mapper
     */
    public static JacksonUtil nonNullMapper() {
        return new JacksonUtil(JsonInclude.Include.NON_NULL);
    }

    /**
     * 将java对象转换成json字符串
     *
     * @param obj 准备转换的对象
     * @return json字符串
     */
    public static String beanToJson(Object obj) throws Exception {
        try {
            ObjectMapper objectMapper = getMapperInstance(false);
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将java对象转换成json字符串
     *
     * @param obj 准备转换的对象
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     * @return json字符串
     */
    public static String beanToJson(Object obj, Boolean createNew) throws Exception {
        try {
            ObjectMapper objectMapper = getMapperInstance(createNew);
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将json字符串转换成java对象
     *
     * @param json 准备转换的json字符串
     * @param cls 准备转换的类
     */
    public static <T> T jsonToBean(String json, Class<T> cls) throws Exception {
        try {
            ObjectMapper objectMapper = getMapperInstance(true);
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 将json字符串转换成java对象
     *
     * @param json 准备转换的json字符串
     * @param cls 准备转换的类
     * @param createNew ObjectMapper实例方式:true，新实例;false,存在的mapper实例
     */
    public static Object jsonToBean(String json, Class<?> cls, Boolean createNew) throws Exception {
        try {
            ObjectMapper objectMapper = getMapperInstance(createNew);
            Object vo = objectMapper.readValue(json, cls);
            return vo;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 将json串转成 JsonNode
     */
    public static JsonNode getJsonNode(String json) throws IOException {
        try {
            json = json.replaceAll("\r|\n|\t", "");
            ObjectMapper objectMapper = getMapperInstance(false);
            return objectMapper.reader().readTree(json);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        ObjectMapper objectMapper = getMapperInstance(false);
        return objectMapper.convertValue(fromValue, toValueType);
    }
}
