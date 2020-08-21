package com.rcloud.server.sealtalk.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`groups`")
public class Groups implements Serializable {

    //关闭群组认证状态标示
    public static final Integer CERTI_STATUS_CLOSED = 1;
    //开启群组认证状态标示
    public static final Integer CERTI_STATUS_OPENED = 0;

    //全员禁言状态 否
    public static final Integer MUTE_STATUS_CLOSE = 0;

    //全员禁言状态 是
    public static final Integer MUTE_STATUS_OPENED = 1;


    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    @Column(name = "portraitUri")
    private String portraitUri;

    @Column(name = "memberCount")
    private Integer memberCount;

    @Column(name = "maxMemberCount")
    private Integer maxMemberCount;

    @Column(name = "creatorId")
    private Integer creatorId;

    @Column(name = "certiStatus")
    private Integer certiStatus;

    @Column(name = "isMute")
    private Integer isMute;

    @Column(name = "clearStatus")
    private Integer clearStatus;

    @Column(name = "clearTimeAt")
    private Long clearTimeAt;

    @Column(name = "memberProtection")
    private Integer memberProtection;

    @Column(name = "copiedTime")
    private Long copiedTime;

    private Long timestamp;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "deletedAt")
    private Date deletedAt;

    @Column(name = "bulletin")
    private String bulletin;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getMaxMemberCount() {
        return maxMemberCount;
    }

    public void setMaxMemberCount(Integer maxMemberCount) {
        this.maxMemberCount = maxMemberCount;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getCertiStatus() {
        return certiStatus;
    }

    public void setCertiStatus(Integer certiStatus) {
        this.certiStatus = certiStatus;
    }

    public Integer getIsMute() {
        return isMute;
    }

    public void setIsMute(Integer isMute) {
        this.isMute = isMute;
    }

    public Integer getClearStatus() {
        return clearStatus;
    }

    public void setClearStatus(Integer clearStatus) {
        this.clearStatus = clearStatus;
    }

    public Long getClearTimeAt() {
        return clearTimeAt;
    }

    public void setClearTimeAt(Long clearTimeAt) {
        this.clearTimeAt = clearTimeAt;
    }

    public Integer getMemberProtection() {
        return memberProtection;
    }

    public void setMemberProtection(Integer memberProtection) {
        this.memberProtection = memberProtection;
    }

    public Long getCopiedTime() {
        return copiedTime;
    }

    public void setCopiedTime(Long copiedTime) {
        this.copiedTime = copiedTime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }
}