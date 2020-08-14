package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.exception.ServiceException;
import io.rong.models.Result;
import io.rong.models.message.GroupMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.BlackListResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description: 调用融云服务客户端
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface RongCloudClient {

    /**
     * 注册并获取token
     *
     * @param encodeId 用户id  n3d编码
     * @param name     昵称
     * @param portrait 头像地址
     * @return
     * @throws ServiceException
     */
    TokenResult register(String encodeId, String name, String portrait) throws ServiceException;

    /**
     * 修改用户信息
     *
     * @param encodeId 用户id
     * @param name     昵称
     * @param portrait 头像地址
     * @return
     * @throws ServiceException
     */
    Result updateUser(String encodeId, String name, String portrait) throws ServiceException;

    /**
     * 获取用户信息
     *
     * @param encodeId 用户id
     * @return
     * @throws ServiceException
     */
    UserResult getUserInfo(String encodeId) throws ServiceException;

    /**
     * 用户添加黑名单
     *
     * @param encodeId
     * @param encodeBlackUserIds
     * @return
     * @throws ServiceException
     */
    public Result addUserBlackList(String encodeId, String[] encodeBlackUserIds) throws ServiceException;

    /**
     * 查询用户黑名单
     *
     * @param encodeId
     * @return
     * @throws ServiceException
     */
    BlackListResult queryUserBlackList(String encodeId) throws ServiceException;


    /**
     * 用户移除黑名单
     *
     * @param encodeId
     * @param encodeBlackUserIds
     * @return
     * @throws ServiceException
     */
    Result removeUserBlackList(String encodeId, String[] encodeBlackUserIds) throws ServiceException;


    /**
     * 发送通知
     *
     * @param encodeCurrentUserId
     * @param currentUserNickName
     * @param encodeFriendId
     * @param contactOperationType
     * @param message
     * @param timestamp
     */
    void sendContactNotification(String encodeCurrentUserId, String currentUserNickName, String encodeFriendId, String contactOperationType, String message, long timestamp);


    //TODO
    ResponseResult sendPrivateMessage(PrivateMessage privateMessage) throws ServiceException;

    //TODO
    ResponseResult sendGroupMessage(GroupMessage groupMessage) throws ServiceException;

    /**
     * 创建群组 TODO
     *
     * @param encodeGroupId
     * @param encodeMemberIds
     * @param name
     */
    Result createGroup(String encodeGroupId, List<String> encodeMemberIds, String name);


    /**
     * 用户加入指定群组 TODO
     *
     * @param encodedMemberIds
     * @param encodedGroupId
     * @param groupName
     * @return
     */
    Result joinGroup(String[] encodedMemberIds, String encodedGroupId, String groupName);

    /**
     * 刷新群组名称 TODO
     *
     * @param encodedGroupId
     * @param name
     * @return
     */
    Result refreshGroupName(String encodedGroupId, String name);

    /**
     * 移除白名单 TODO
     *
     * @param encodedGroupId
     * @param encodedMemberIds
     * @return
     */
    Result removeGroupWhiteList(String encodedGroupId, String[] encodedMemberIds);

    /**
     * 新增白名单 TODO
     *
     * @param encodedGroupId
     * @param encodedMemberIds
     * @return
     */
    Result addGroupWhitelist(String encodedGroupId, String[] encodedMemberIds);

    /**
     * 解散群组
     *
     * @param encodeUserId
     * @param encodedGroupId
     * @return
     */
    Result dismiss(String encodeUserId, String encodedGroupId);

    /**
     * 用户退群
     *
     * @param encodedMemberIds
     * @param encodedGroupId
     * @return
     */
    Result quitGroup(String[] encodedMemberIds, String encodedGroupId);
}
