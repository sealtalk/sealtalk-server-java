package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.GroupManager;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.model.response.dto.GroupAddStatusDTO;
import com.rcloud.server.sealtalk.model.response.dto.UserStatusDTO;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "群组相关")
@RestController
@RequestMapping("/group")
@Timed(percentiles = {0.9, 0.95, 0.99})
@Slf4j
public class GroupController extends BaseController {

    @Resource
    private GroupManager groupManager;


    @ApiOperation(value = "创建群组")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResult<Object> create(
            @ApiParam(name = "name", value = "群名称", required = true, type = "String", example = "86")
            @RequestParam String name,
            @ApiParam(name = "memberIds", value = "群成员 Id 列表, 包含 创建者 Id", required = true, type = "String", example = "18811111111")
            @RequestParam String[] memberIds,
            @ApiParam(name = "portraitUri", value = "头像地址", required = false, type = "String", example = "18811111111")
            @RequestParam String portraitUri,
            HttpServletRequest request
    ) throws ServiceException {

        name = MiscUtils.xss(name, ValidateUtils.GROUP_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupName(name);
        ValidateUtils.checkMemberIds(memberIds);

        Integer currentUserId = getCurrentUserId(request);

        GroupAddStatusDTO groupAddStatusDTO = groupManager.createGroup(currentUserId, name, memberIds, portraitUri);
        return APIResultWrap.ok(MiscUtils.encodeResults(groupAddStatusDTO));
    }


    @ApiOperation(value = "添加群成员")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public APIResult<Object> addMember(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "memberIds", value = "群成员 Id 列表", required = true, type = "String", example = "18811111111")
            @RequestParam String[] memberIds,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        if (ArrayUtils.isEmpty(memberIds)) {
            throw new ServiceException(ErrorCode.GROUP_LIMIT_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);
        List<UserStatusDTO> userStatusDTOList = groupManager.addMember(currentUserId, groupId, memberIds);

        return APIResultWrap.ok(MiscUtils.encodeResults(userStatusDTOList));
    }
}
