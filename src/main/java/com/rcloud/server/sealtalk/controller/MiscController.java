package com.rcloud.server.sealtalk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.domain.ScreenStatuses;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.GroupManager;
import com.rcloud.server.sealtalk.manager.MiscManager;
import com.rcloud.server.sealtalk.model.dto.DemoSquareDTO;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.CacheUtil;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "其他相关")
@RestController
@RequestMapping("/misc")
@Timed(percentiles = {0.9, 0.95, 0.99})
@Slf4j
public class MiscController extends BaseController {


    @Value("classpath:squirrel.json")
    private org.springframework.core.io.Resource squirrelResource;

    @Value("classpath:client_version.json")
    private org.springframework.core.io.Resource clientResource;

    @Value("classpath:demo_square.json")
    private org.springframework.core.io.Resource demoSquareResource;

    @Autowired
    private GroupManager groupManager;

    @Autowired
    private MiscManager miscManager;

    @ApiOperation(value = "获取客户端最新版本（ Desktop 使用 ）")
    @RequestMapping(value = "/latest_update", method = RequestMethod.GET)
    public void getLatestUpdateVersion(
            @ApiParam(name = "version", value = "版本号", required = true, type = "String", example = "xxx")
            @RequestParam("version") String version,
            HttpServletResponse response) throws ServiceException, IOException {

        try {
            response.setCharacterEncoding("utf8");
            if (StringUtils.isEmpty(version)) {
                response.setStatus(400);
                response.getWriter().write("Invalid version.");
                return;
            }

            String result = CacheUtil.get(CacheUtil.LAST_UPDATE_VERSION_INFO);
            if (StringUtils.isEmpty(result)) {
                String jsonData = IOUtils
                        .toString(squirrelResource.getInputStream(), StandardCharsets.UTF_8);
                result = jsonData;

            }

            String jsonVersion = "";
            JsonNode jsonNode = JacksonUtil.getJsonNode(result);
            if (jsonNode != null) {
                JsonNode v = jsonNode.get("version");
                if (v.isNull()) {
                    response.setStatus(400);
                    response.getWriter().write("Invalid version.");
                    return;
                } else {
                    jsonVersion = v.asText();
                }
            }

            if (version.compareTo(jsonVersion) > 0) {
                response.setStatus(204);
                return;
            }

            CacheUtil.set(CacheUtil.LAST_UPDATE_VERSION_INFO, result);
            response.getWriter().write(result);
            return;
        } catch (IOException e) {
            response.setStatus(500);
            response.getWriter().write("server error.");
        }
    }


    @ApiOperation(value = "Android、iOS 获取更新版本")
    @RequestMapping(value = "/client_version", method = RequestMethod.GET)
    public void getClientVersion(HttpServletResponse response) throws ServiceException, IOException {
        try {
            response.setCharacterEncoding("utf8");
            String result = CacheUtil.get(CacheUtil.CLIENT_VERSION_INFO);
            if (StringUtils.isEmpty(result)) {
                String jsonData = IOUtils
                        .toString(clientResource.getInputStream(), StandardCharsets.UTF_8);
                result = jsonData;

            }
            CacheUtil.set(CacheUtil.CLIENT_VERSION_INFO, result);
            response.getWriter().write(result);
            return;
        } catch (IOException e) {
            response.setStatus(500);
            response.getWriter().write("server error.");
        }
    }

    /**
     * Android、iOS 获取更新版本, 返回 sealtalk 标准数据格式
     *
     * @param response
     * @return
     */
    @ApiOperation(value = "Android、iOS 获取更新版本")
    @RequestMapping(value = "/mobile_version", method = RequestMethod.GET)
    public APIResult<?> getMobileVersion(HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf8");

            String result = CacheUtil.get(CacheUtil.MOBILE_VERSION_INFO);
            if (StringUtils.isEmpty(result)) {
                String jsonData = IOUtils
                        .toString(clientResource.getInputStream(), StandardCharsets.UTF_8);
                result = jsonData;

            }
            CacheUtil.set(CacheUtil.MOBILE_VERSION_INFO, result);

            return APIResultWrap.ok(JacksonUtil.getJsonNode(result));
        } catch (Exception e) {
            return APIResultWrap.error(ErrorCode.SERVER_ERROR);
        }
    }


    @ApiOperation(value = "Android、iOS 获取更新版本")
    @RequestMapping(value = "/demo_square", method = RequestMethod.GET)
    public APIResult<?> getDemoSquare() {
        try {

            String jsonData = IOUtils
                    .toString(demoSquareResource.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            List<DemoSquareDTO> demoSquareDTOList = objectMapper.readValue(jsonData, new TypeReference<List<DemoSquareDTO>>() {
            });

            List<Integer> groupIds = new ArrayList<>();
            if (CollectionUtils.isEmpty(demoSquareDTOList)) {
                return APIResultWrap.ok(null);
            }

            for (DemoSquareDTO demoSquareDTO : demoSquareDTOList) {
                groupIds.add(demoSquareDTO.getId());
            }
            List<Groups> groupsList = groupManager.getGroupList(groupIds);

            Map<Integer, Groups> groupsMap = new HashMap<>();
            if (groupsList != null) {
                for (Groups groups : groupsList) {
                    groupsMap.put(groups.getId(), groups);
                }
            }

            for (DemoSquareDTO demoSquareDTO : demoSquareDTOList) {
                if ("group".equals(demoSquareDTO.getType())) {
                    Groups groups = groupsMap.get(demoSquareDTO.getId());
                    if (groups == null) {
                        demoSquareDTO.setName("Unknown");
                        demoSquareDTO.setPortraitUri("");
                        demoSquareDTO.setMemberCount(0);
                    } else {
                        demoSquareDTO.setName(groups.getName());
                        demoSquareDTO.setPortraitUri(groups.getPortraitUri());
                        demoSquareDTO.setMemberCount(groups.getMemberCount());
                        demoSquareDTO.setMaxMemberCount(groups.getMaxMemberCount());
                    }
                }
            }
            return APIResultWrap.ok(MiscUtils.encodeResults(demoSquareDTOList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return APIResultWrap.error(ErrorCode.SERVER_ERROR);
        }
    }


    @ApiOperation(value = "Server API 发送消息")
    @RequestMapping(value = "/send_message", method = RequestMethod.POST)
    public APIResult<Object> sendMessage(@ApiParam(name = "conversationType", value = "会话类型 PRIVATE GROUP", required = true, type = "String", example = "xxx")
                                         @RequestParam("conversationType") String conversationType,
                                         @ApiParam(name = "targetId", value = "接收者 Id", required = true, type = "String", example = "xxx")
                                         @RequestParam("targetId") String targetId,
                                         @ApiParam(name = "objectName", value = "消息类型 RC:TxtMsg RC:ImgMsg", required = true, type = "String", example = "xxx")
                                         @RequestParam("objectName") String objectName,
                                         @ApiParam(name = "content", value = "消息内容", required = true, type = "String", example = "xxx")
                                         @RequestParam("content") String content,
                                         @ApiParam(name = "pushContent", value = "push 内容", required = false, type = "String", example = "xxx")
                                         @RequestParam("pushContent") String pushContent,
                                         @ApiParam(name = "encodedTargetId", value = "encodedTargetId", required = true, type = "String", example = "xxx")
                                         @RequestParam("encodedTargetId") String encodedTargetId,
                                         HttpServletRequest request) throws ServiceException {

        ValidateUtils.notEmpty(conversationType);
        ValidateUtils.notEmpty(targetId);
        ValidateUtils.notEmpty(objectName);
        ValidateUtils.notEmpty(content);
        ValidateUtils.notEmpty(encodedTargetId);

        Integer currentUserId = getCurrentUserId(request);
        miscManager.sendMessage(currentUserId, conversationType, targetId, objectName, content, pushContent, encodedTargetId);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "截屏通知状态设置")
    @RequestMapping(value = "/set_screen_capture", method = RequestMethod.POST)
    public APIResult<Object> setScreenCapture(@ApiParam(name = "conversationType", value = "会话类型：1 单聊、3 群聊", required = true, type = "Integer", example = "xxx")
                                              @RequestParam("conversationType") Integer conversationType,
                                              @ApiParam(name = "targetId", value = "接收者 Id", required = true, type = "String", example = "xxx")
                                              @RequestParam("targetId") Integer targetId,
                                              @ApiParam(name = "noticeStatus", value = "设置状态： 0 关闭 1 打开", required = true, type = "Integer", example = "xxx")
                                              @RequestParam("noticeStatus") Integer noticeStatus,
                                              HttpServletRequest request) throws ServiceException {

        ValidateUtils.notNull(conversationType);
        ValidateUtils.notNull(targetId);
        ValidateUtils.notNull(noticeStatus);

        Integer currentUserId = getCurrentUserId(request);

        miscManager.setScreenCapture(currentUserId, targetId, conversationType, noticeStatus);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取截屏通知状态")
    @RequestMapping(value = "/get_screen_capture", method = RequestMethod.POST)
    public APIResult<Object> getScreenCapture(@ApiParam(name = "conversationType", value = "会话类型：1 单聊、3 群聊", required = true, type = "Integer", example = "xxx")
                                              @RequestParam("conversationType") Integer conversationType,
                                              @ApiParam(name = "targetId", value = "接收者 Id", required = true, type = "String", example = "xxx")
                                              @RequestParam("targetId") String targetId,
                                              HttpServletRequest request) throws ServiceException {

        ValidateUtils.notNull(conversationType);
        ValidateUtils.notNull(targetId);

        Integer currentUserId = getCurrentUserId(request);

        ScreenStatuses screenStatuses = miscManager.getScreenCapture(currentUserId, targetId, conversationType);
        Map<String, Object> result = new HashMap<>();
        if (screenStatuses == null) {
            result.put("status", 0);
        } else {
            result.put("status", screenStatuses.getStatus());
        }
        return APIResultWrap.ok(MiscUtils.encodeResults(result));
    }

    @ApiOperation(value = "发送截屏通知消息")
    @RequestMapping(value = "/send_sc_msg", method = RequestMethod.POST)
    public APIResult<Object> sendScreenCaptureMsg(@ApiParam(name = "conversationType", value = "会话类型：1 单聊、3 群聊", required = true, type = "Integer", example = "xxx")
                                                  @RequestParam("conversationType") Integer conversationType,
                                                  @ApiParam(name = "targetId", value = "接收者 Id", required = true, type = "String", example = "xxx")
                                                  @RequestParam("targetId") String targetId,
                                                  HttpServletRequest request) throws ServiceException {

        ValidateUtils.notNull(conversationType);
        ValidateUtils.notNull(targetId);

        Integer currentUserId = getCurrentUserId(request);

        miscManager.sendScreenCaptureMsg(currentUserId, targetId, conversationType);

        return APIResultWrap.ok("");
    }


}
