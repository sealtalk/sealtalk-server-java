package com.rcloud.server.sealtalk.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 入群前待审核表
 */
@Table(name = "group_receivers")
public class GroupReceivers implements Serializable {

    public static final Integer GROUP_RECEIVE_STATUS_IGNORE = 0;    //忽略
    public static final Integer GROUP_RECEIVE_STATUS_AGREED = 1;    //同意
    public static final Integer GROUP_RECEIVE_STATUS_WAIT = 2;      //等待
    public static final Integer GROUP_RECEIVE_STATUS_EXPIRED = 3;   //过期

    public static final Integer GROUP_RECEIVE_TYPE_MEMBER = 1;      //群普通成员
    public static final Integer GROUP_RECEIVE_TYPE_MANAGER = 2;     //群管理者

    @Id
    private Integer id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "groupId")
    private Integer groupId;

    @Column(name = "groupName")
    private String groupName;

    @Column(name = "groupPortraitUri")
    private String groupPortraitUri;

    @Column(name = "requesterId")
    private Integer requesterId;

    @Column(name = "receiverId")
    private Integer receiverId;

    private Integer type;

    private Integer status;

    @Column(name = "deletedUsers")
    private String deletedUsers;

    @Column(name = "isRead")
    private Integer isRead;

    @Column(name = "joinInfo")
    private String joinInfo;

    private Long timestamp;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Transient
    private Users requester;
    @Transient
    private Users receiver;
    @Transient
    private Groups group;


    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * @return groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return groupPortraitUri
     */
    public String getGroupPortraitUri() {
        return groupPortraitUri;
    }

    /**
     * @param groupPortraitUri
     */
    public void setGroupPortraitUri(String groupPortraitUri) {
        this.groupPortraitUri = groupPortraitUri;
    }

    /**
     * @return requesterId
     */
    public Integer getRequesterId() {
        return requesterId;
    }

    /**
     * @param requesterId
     */
    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    /**
     * @return receiverId
     */
    public Integer getReceiverId() {
        return receiverId;
    }

    /**
     * @param receiverId
     */
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return deletedUsers
     */
    public String getDeletedUsers() {
        return deletedUsers;
    }

    /**
     * @param deletedUsers
     */
    public void setDeletedUsers(String deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    /**
     * @return isRead
     */
    public Integer getIsRead() {
        return isRead;
    }

    /**
     * @param isRead
     */
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    /**
     * @return joinInfo
     */
    public String getJoinInfo() {
        return joinInfo;
    }

    /**
     * @param joinInfo
     */
    public void setJoinInfo(String joinInfo) {
        this.joinInfo = joinInfo;
    }

    /**
     * @return timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Users getRequester() {
        return requester;
    }

    public void setRequester(Users requester) {
        this.requester = requester;
    }

    public Users getReceiver() {
        return receiver;
    }

    public void setReceiver(Users receiver) {
        this.receiver = receiver;
    }

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }
}