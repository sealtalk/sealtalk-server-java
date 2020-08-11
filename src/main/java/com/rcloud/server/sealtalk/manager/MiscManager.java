package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import com.rcloud.server.sealtalk.domain.ScreenStatuses;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.FriendshipsService;
import com.rcloud.server.sealtalk.service.GroupMembersService;
import com.rcloud.server.sealtalk.service.ScreenStatusesService;
import com.rcloud.server.sealtalk.service.UsersService;
import com.rcloud.server.sealtalk.util.N3d;
import io.rong.messages.TxtMessage;
import io.rong.models.message.GroupMessage;
import io.rong.models.message.PrivateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class MiscManager extends BaseManager {

    @Resource
    private RongCloudClient rongCloudClient;

    @Resource
    private FriendshipsService friendshipsService;

    @Resource
    private GroupMembersService groupMembersService;

    @Resource
    private UsersService usersService;

    @Resource
    private ScreenStatusesService screenStatusesService;


    /**
     * 调用Server api发送消息
     *
     * @param conversationType
     * @param targetId
     * @param objectName
     * @param content
     * @param pushContent
     * @param encodedTargetId
     */
    public void sendMessage(Integer currentUserId, String conversationType, String targetId, String objectName, String content, String pushContent, String encodedTargetId) throws ServiceException {
        if (Constants.CONVERSATION_TYPE_PRIVATE.equals(conversationType)) {
            //如果会话类型是单聊
            Example example = new Example(Friendships.class);
            example.createCriteria().andEqualTo("userId", currentUserId)
                    .andEqualTo("friendId", targetId)
                    .andEqualTo("status", Friendships.FRIENDSHIP_AGREED);
            Friendships friendships = friendshipsService.getOneByExample(example);

            if (friendships != null) {
                //调用融云接口发送单聊消息
                PrivateMessage privateMessage = new PrivateMessage()
                        .setSenderId(N3d.encode(currentUserId))
                        .setTargetId(new String[]{encodedTargetId})
                        .setObjectName(objectName)
                        //TODO
                        .setContent(new TxtMessage(content, ""))
                        .setPushContent(pushContent);
                rongCloudClient.sendPrivateMessage(privateMessage);
                return;
            } else {
                throw new ServiceException(ErrorCode.NOT_YOUR_FRIEND);
            }
        } else if (Constants.CONVERSATION_TYPE_GROUP.equals(conversationType)) {
            //如果会话类型是群组
            Example example = new Example(GroupMembers.class);
            example.createCriteria().andEqualTo("groupId", targetId)
                    .andEqualTo("memberId", currentUserId);
            GroupMembers groupMembers = groupMembersService.getOneByExample(example);

            if (groupMembers != null) {
                GroupMessage groupMessage = new GroupMessage();
                groupMessage.setSenderId(N3d.encode(currentUserId))
                        .setTargetId(new String[]{targetId})
                        .setObjectName(objectName)
                        //TODO
                        .setContent(new TxtMessage(content, ""))
                        .setPushContent(pushContent);
                //发送群组消息
                rongCloudClient.sendGroupMessage(groupMessage);
            } else {
                //TODO
                throw new ServiceException(ErrorCode.NOT_YOUR_GROUP);
            }
        } else {
            throw new ServiceException(ErrorCode.UNSUPPORTED_CONVERSATION_TYPE);
        }
    }

    /**
     * 设置截屏通知状态
     *
     * @param currentUserId
     * @param targetId
     * @param conversationType
     * @param noticeStatus
     */
    public void setScreenCapture(Integer currentUserId, Integer targetId, Integer conversationType, Integer noticeStatus) throws ServiceException {

        String operateId = String.valueOf(targetId);
        String statusContent = noticeStatus == 0 ? "closeScreenNtf" : "openScreenNtf";
        if (conversationType == 1) {
            operateId = currentUserId < targetId ? currentUserId + "_" + targetId : targetId + "_" + currentUserId;
        }

        Users users = usersService.getByPrimaryKey(currentUserId);
        if (users != null) {
            Example example = new Example(ScreenStatuses.class);
            example.createCriteria().andEqualTo("conversationType", conversationType)
                    .andEqualTo("operateId", operateId);

            ScreenStatuses screenStatuses = screenStatusesService.getOneByExample(example);

            if (screenStatuses != null) {
                //如果存在截屏设置记录则更新状态
                screenStatuses.setStatus(noticeStatus);
                screenStatusesService.updateByPrimaryKeySelective(screenStatuses);
                //发送截屏消息
                sendScreenMsg0(currentUserId, targetId, conversationType, statusContent);
            } else {
                //如果不存在，则创建
                ScreenStatuses ss = new ScreenStatuses();
                ss.setOperateId(operateId);
                ss.setConversationType(conversationType);
                ss.setStatus(noticeStatus);
                ss.setCreatedAt(new Date());
                ss.setUpdatedAt(ss.getCreatedAt());
                screenStatusesService.saveSelective(ss);
                //发送截屏通知消息
                sendScreenMsg0(currentUserId, targetId, conversationType, statusContent);
            }
        } else {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }


    }

    /**
     * 发送截屏通知消息
     *
     * @param currentUserId
     * @param targetId
     * @param conversationType
     * @param statusContent
     */

    private void sendScreenMsg0(Integer currentUserId, Integer targetId, Integer conversationType, String statusContent) {
        //TODO
    }

    /**
     * 获取截屏通知状态
     *
     * @param currentUserId
     * @param targetId
     * @param conversationType
     * @return
     */
    public ScreenStatuses getScreenCapture(Integer currentUserId, String targetId, Integer conversationType) throws ServiceException {
        String operateId = targetId;
        if (conversationType == 1) {
            operateId = currentUserId < Integer.valueOf(targetId) ? currentUserId + "_" + targetId : targetId + "_" + currentUserId;
        }

        Example example = new Example(ScreenStatuses.class);
        example.createCriteria().andEqualTo("operateId", operateId)
                .andEqualTo("conversationType", conversationType);
        return screenStatusesService.getOneByExample(example);
    }


    /**
     * 发送截屏消息
     *
     * @param currentUserId
     * @param targetId
     * @param conversationType
     */
    public void sendScreenCaptureMsg(Integer currentUserId, String targetId, Integer conversationType) {
        sendScreenMsg0(currentUserId, Integer.valueOf(targetId), conversationType, "sendScreenNtf");
    }
}
