package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "group_exited_lists")
public class GroupExitedLists implements Serializable {
    @Id
    private Integer id;

    private Integer groupId;

    private Integer quitUserId;

    private String quitNickname;

    private String quitPortraitUri;

    private Integer quitReason;

    private Long quitTime;

    private Integer operatorId;

    private String operatorName;

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
     * @return quitUserId
     */
    public Integer getQuitUserId() {
        return quitUserId;
    }

    /**
     * @param quitUserId
     */
    public void setQuitUserId(Integer quitUserId) {
        this.quitUserId = quitUserId;
    }

    /**
     * @return quitNickname
     */
    public String getQuitNickname() {
        return quitNickname;
    }

    /**
     * @param quitNickname
     */
    public void setQuitNickname(String quitNickname) {
        this.quitNickname = quitNickname;
    }

    /**
     * @return quitPortraitUri
     */
    public String getQuitPortraitUri() {
        return quitPortraitUri;
    }

    /**
     * @param quitPortraitUri
     */
    public void setQuitPortraitUri(String quitPortraitUri) {
        this.quitPortraitUri = quitPortraitUri;
    }

    /**
     * @return quitReason
     */
    public Integer getQuitReason() {
        return quitReason;
    }

    /**
     * @param quitReason
     */
    public void setQuitReason(Integer quitReason) {
        this.quitReason = quitReason;
    }

    /**
     * @return quitTime
     */
    public Long getQuitTime() {
        return quitTime;
    }

    /**
     * @param quitTime
     */
    public void setQuitTime(Long quitTime) {
        this.quitTime = quitTime;
    }

    /**
     * @return operatorId
     */
    public Integer getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId
     */
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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