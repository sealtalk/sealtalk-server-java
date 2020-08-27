package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.rongcloud.message.CustomerGroupApplyMessage;
import io.rong.models.Result;
import io.rong.models.message.GroupMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.BlackListResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;

import java.util.List;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description: 调用融云服务客户端
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface RongCloudClient {


    /**
     * 注册并获取token 通过调用sdk的方式
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
     * @param senderId       发送人
     * @param nickname       发送人昵称
     * @param targetIds      这条消息的接收人
     * @param toUserId       operatoion操作对应的人，比如添加好友动作的好友
     * @param operation      操作
     * @param messageContent 消息内容
     * @param timestamp      版本
     * @throws ServiceException
     */
    public void sendContactNotification(String senderId, String nickname, String[] targetIds, String toUserId, String operation, String messageContent, long timestamp) throws ServiceException;

    //TODO
    ResponseResult sendPrivateMessage(PrivateMessage privateMessage) throws ServiceException;

    //TODO
    ResponseResult sendGroupMessage(GroupMessage groupMessage) throws ServiceException;

    /**
     * 创建群组
     *
     * @param encodeGroupId   群组ID
     * @param encodeMemberIds 成员ID
     * @param name            群名称
     */
    Result createGroup(String encodeGroupId, String[] encodeMemberIds, String name) throws ServiceException;


    /**
     * 用户加入指定群组 TODO
     *
     * @param encodedMemberIds
     * @param encodedGroupId
     * @param groupName
     * @return
     */
    Result joinGroup(String[] encodedMemberIds, String encodedGroupId, String groupName) throws ServiceException;

    /**
     * 刷新群组名称 TODO
     *
     * @param encodedGroupId
     * @param name
     * @return
     */
    Result refreshGroupName(String encodedGroupId, String name) throws ServiceException;

    /**
     * 移除群禁言白名单
     *
     * @param encodedGroupId
     * @param encodedMemberIds
     * @return
     */
    Result removeGroupWhiteList(String encodedGroupId, String[] encodedMemberIds) throws ServiceException;

    /**
     * 新增群禁言白名单
     *
     * @param encodedGroupId
     * @param encodedMemberIds
     * @return
     */
    Result addGroupWhitelist(String encodedGroupId, String[] encodedMemberIds) throws ServiceException;

    /**
     * 解散群组
     *
     * @param encodeUserId
     * @param encodedGroupId
     * @return
     */
    Result dismiss(String encodeUserId, String encodedGroupId) throws ServiceException;

    /**
     * 用户退群
     *
     * @param encodedMemberIds
     * @param encodedGroupId
     * @param groupName
     * @return
     */
    Result quitGroup(String[] encodedMemberIds, String encodedGroupId, String groupName) throws ServiceException;

    /**
     * 取消群组禁言
     *
     * @param encodeGroupIds
     * @return
     */
    Result removeMuteStatus(String[] encodeGroupIds) throws ServiceException;

    /**
     * 设置群组禁言
     *
     * @param encodeGroupIds
     * @return
     */
    Result setMuteStatus(String[] encodeGroupIds) throws ServiceException;

    /**
     * 发送群组通知消息
     *
     * @param encodeOperatorUserId
     * @param encodeGroupId
     * @param operationType
     * @param messageData
     * @param message
     * @param extra
     * @return
     */
    Result sendGroupNotificationMessage(String encodeOperatorUserId, String encodeGroupId, String operationType, Map<String, Object> messageData, String message, String extra) throws ServiceException;


    /**
     * 发送群组通知消息
     *
     * @param groupMessage
     * @return
     * @throws ServiceException
     */
    Result sendCustomerGroupNtfMessage(GroupMessage groupMessage) throws ServiceException;


    /**
     * 发送群组通知消息 ST:GrpNtf
     *
     * @param encodeUserId
     * @param encodeTargetId
     * @param operation
     * @return
     * @throws ServiceException
     */
    Result sendCustomerGroupNtfMessage(String encodeUserId, String encodeTargetId, String operation) throws ServiceException;


    /**
     * 发送群组通知消息 ST:ConNtf
     *
     * @param encodeUserId
     * @param encodeTargetId
     * @param operation
     * @return
     * @throws ServiceException
     */
    Result sendCustomerConNtfMessage(String encodeUserId, String encodeTargetId, String operation) throws ServiceException;


    /**
     * 发送群组申请消息
     *
     * @param senderId
     * @param targetId
     * @param grpApplyMessage
     * @return
     * @throws ServiceException
     */
    Result sendGroupApplyMessage(String senderId, String[] targetId, CustomerGroupApplyMessage grpApplyMessage) throws ServiceException;

    /**
     * 同步用户组群信息
     *
     * @param encodeUserId
     * @param groupsList
     * @return
     */
    Result syncGroupInfo(String encodeUserId, List<Groups> groupsList) throws ServiceException;


    /**
     * 发送群公告通知
     *
     * @param fromUserId
     * @param toGroupId
     * @param content
     * @param content          公告内容
     * @param type             类型，1 表示 @ 所有人、2 表示 @ 指定用户
     * @param userIds          type 为 2 时有效，为 1 时 userIdList 可以为空
     * @param mentionedContent @ 消息的自定义 Push 内容
     * @return
     * @throws ServiceException
     */
    Result sendBulletinNotification(String fromUserId, String[] toGroupId, String content, Integer type, String[] userIds, String mentionedContent) throws ServiceException;

}
