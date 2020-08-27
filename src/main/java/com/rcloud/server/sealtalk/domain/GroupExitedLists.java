package com.rcloud.server.sealtalk.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "group_exited_lists")
public class GroupExitedLists implements Serializable {

    public static final Integer QUITE_REASON_CREATOR = 0;   //群主踢出
    public static final Integer QUITE_REASON_MANAGER = 1;   //管理员踢出
    public static final Integer QUITE_REASON_SELF = 2;      //主动退出

    @Id
    private Integer id;

    @Column(name = "groupId")
    private Integer groupId;

    @Column(name = "quitUserId")
    private Integer quitUserId;

    @Column(name = "quitNickname")
    private String quitNickname;

    @Column(name = "quitPortraitUri")
    private String quitPortraitUri;

    @Column(name = "quitReason")
    private Integer quitReason;

    @Column(name = "quitTime")
    private Long quitTime;

    @Column(name = "operatorId")
    private Integer operatorId;

    @Column(name = "operatorName")
    private String operatorName;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
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