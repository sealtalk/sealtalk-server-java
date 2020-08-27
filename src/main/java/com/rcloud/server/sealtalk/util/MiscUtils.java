package com.rcloud.server.sealtalk.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
public class MiscUtils {

    /**
     * 地区、标示map
     */
    private static Map<String, String> regionMap = new HashMap<>();

    static {
        regionMap.put("86", "zh-CN");
    }


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

    public static String hash(String text, int salt) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        else {
            text = text + "|" + salt;
            return DigestUtils.sha1Hex(text);
        }
    }


    public static String merge(String content, String key, String code) {
        content = content.replaceAll(key, code);
        return content;
    }

    public static String getRegionName(String region) {
        return regionMap.get(region);
    }


    /**
     * 文本xss处理
     *
     * @param str
     * @param maxLength
     * @return
     */
    public static String xss(String str, int maxLength) {
        String result = "";
        if (StringUtils.isEmpty(str)) {
            return result;
        }
        result = StringEscapeUtils.escapeHtml4(str);
        if (result.length() > maxLength) {
            result = result.substring(0, maxLength);
        }
        return result;

    }

    /**
     * 根据propertyExpression 对结果对象中的ID进行N3D编码
     * <p>
     * propertyExpression 用点 "." 导航，如下
     * <p>
     * Object{
     * userId：     //propertyExpression=userId
     * groups{
     * id:1   // propertyExpression = groups.id
     * }
     * <p>
     * [           //  如果是数组或list同上
     * groups{
     * id   // propertyExpression = groups.id
     * },
     * groups{
     * id
     * }
     * ]
     * }
     * <p>
     * 如果参数propertyExpression 为空默认为 propertyExpression = "id"
     *
     * @param o
     * @param propertyExpressions
     * @return
     */
    public static Object encodeResults(Object o, String... propertyExpressions) throws ServiceException {
        try {
            if (o == null) {
                return null;
            }
            if (propertyExpressions == null || propertyExpressions.length == 0) {
                //默认对ID进行加密
                propertyExpressions = new String[]{"id"};
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsBytes(o));

            for (String propertyExpression : propertyExpressions) {
                processResult(jsonNode, propertyExpression);
            }
            return jsonNode;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    public static String addUpdateTimeToList(String result) {
        //TODO
        return result;
    }

    private static void processResult(JsonNode jsonNode, String propertyExpression) throws ServiceException {
        if (jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.iterator();
            while (it.hasNext()) {
                JsonNode jsonNode1 = it.next();
                processResult(jsonNode1, propertyExpression);
            }
        } else {
            String[] elements = propertyExpression.split("\\.");
            JsonNode targetNode = null;

            if (elements.length == 1) {
                targetNode = jsonNode;
            } else {
                int index = 0;
                for (int i = 0; i < elements.length - 1; i++) {
                    targetNode = jsonNode.get(elements[i]);
                    index = index + elements[i].length() + 1;
                    if (targetNode != null && targetNode.isArray()) {
                        processResult(targetNode, propertyExpression.substring(index));
                        return;
                    }
                    if (targetNode == null || targetNode.isNull()) {
                        return;
                    }
                }
            }
            ObjectNode objectNode = (ObjectNode) targetNode;
            if (objectNode.get(elements[elements.length - 1]) != null) {
                if (!objectNode.get(elements[elements.length - 1]).isNull()) {
                    objectNode.put(elements[elements.length - 1], N3d.encode(objectNode.get(elements[elements.length - 1]).asInt()));
                }
            }

            return;
        }
    }

    public static void main(String[] args) throws Exception {

        List<Users> usersList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Users users = new Users();
            users.setId(12 + i);
            users.setNickname("test22" + i);
            users.setPhone("18810183283");
            users.setDeletedAt(new Date());
            Groups groups = new Groups();
            groups.setId(null);
            groups.setCreatorId(33 + i);
            groups.setCreatedAt(new Date());
            groups.setName("gnameTest");
            users.setGroups(groups);
            usersList.add(users);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Object object = MiscUtils.encodeResults(usersList);
        System.out.println(objectMapper.writeValueAsString(APIResultWrap.ok(object)));

        Map<String, Object> map = new HashMap<>();
        map.put("id", 12);
        map.put("name", "test");

        List<Object> list = new ArrayList<>();

        Map<String, Object> map2 = new HashMap<>();
        map2.put("userId", 33);
        map2.put("name", "testname");
        list.add(map2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("userId", 33);
        map3.put("name", "testname");
        map3.put("users", usersList);
        list.add(map3);
        map.put("list", list);
        map.put("users", usersList);
        System.out.println(MiscUtils.encodeResults(map, "list.users.groups.creatorId", "id", "mapGroup.userId", "list.userId", "users.id", "users.groups.id", "users.groups.creatorId"));

        String text = "abcd123";
        int salt = 9988;

        //a2d46a186480138852a18cb1c8b2af530f3e5166
        System.out.println(hash(text, salt));
    }


    /**
     * 单个元素转换成数组
     *
     * @param str
     * @return
     */
    public static String[] one2Array(String str) {
        return new String[]{str};
    }

    public static String[] encodeIds(String[] ids) throws ServiceException {
        if(ArrayUtils.isNotEmpty(ids)){
            String[] result = new String[ids.length];

            for(int i=0;i<ids.length;i++){
                result[i] = N3d.encode(Integer.valueOf(ids[i]));
            }

            return result;
        }
        return null;
    }

    public static Integer[] decodeIds(String[] ids) throws ServiceException {
        if(ArrayUtils.isNotEmpty(ids)){
            Integer[] result = new Integer[ids.length];

            for(int i=0;i<ids.length;i++){
                result[i] = N3d.decode(ids[i]);
            }

            return result;
        }
        return null;
    }

    public static String[] encodeIds(List<Integer> ids) throws ServiceException {
        if(!CollectionUtils.isEmpty(ids)){
            String[] result = new String[ids.size()];

            for(int i=0;i<ids.size();i++){
                result[i] = N3d.encode(ids.get(i));
            }

            return result;
        }
        return null;
    }



    public static Integer[] toInteger(String[] memberIds) {

        if(memberIds!=null){
            Integer[] v = new Integer[memberIds.length];
            for(int i=0;i<memberIds.length;i++){
                v[i] = Integer.valueOf(memberIds[i]);
            }
            return v;
        }
        return null;
    }
}
