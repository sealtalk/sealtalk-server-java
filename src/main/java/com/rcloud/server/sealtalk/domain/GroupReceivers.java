package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "group_receivers")
public class GroupReceivers implements Serializable {
    @Id
    private Integer id;

    private Integer userId;

    private Integer groupId;

    private String groupName;

    private String groupPortraitUri;

    private Integer requesterId;

    private Integer receiverId;

    private Integer type;

    private Integer status;

    private String deletedUsers;

    private Integer isRead;

    private String joinInfo;

    private Long timestamp;

    private Date createdAt;

    private Date updatedAt;

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
}