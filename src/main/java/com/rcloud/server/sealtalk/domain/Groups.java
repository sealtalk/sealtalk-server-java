package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "groups")
public class Groups implements Serializable {
    @Id
    private Integer id;

    private String name;

    private String portraitUri;

    private Integer memberCount;

    private Integer maxMemberCount;

    private Integer creatorId;

    private Integer certiStatus;

    private Integer isMute;

    private Integer clearStatus;

    private Long clearTimeAt;

    private Integer memberProtection;

    private Long copiedTime;

    private Long timestamp;

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private String bulletin;

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return portraitUri
     */
    public String getPortraitUri() {
        return portraitUri;
    }

    /**
     * @param portraitUri
     */
    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    /**
     * @return memberCount
     */
    public Integer getMemberCount() {
        return memberCount;
    }

    /**
     * @param memberCount
     */
    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    /**
     * @return maxMemberCount
     */
    public Integer getMaxMemberCount() {
        return maxMemberCount;
    }

    /**
     * @param maxMemberCount
     */
    public void setMaxMemberCount(Integer maxMemberCount) {
        this.maxMemberCount = maxMemberCount;
    }

    /**
     * @return creatorId
     */
    public Integer getCreatorId() {
        return creatorId;
    }

    /**
     * @param creatorId
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * @return certiStatus
     */
    public Integer getCertiStatus() {
        return certiStatus;
    }

    /**
     * @param certiStatus
     */
    public void setCertiStatus(Integer certiStatus) {
        this.certiStatus = certiStatus;
    }

    /**
     * @return isMute
     */
    public Integer getIsMute() {
        return isMute;
    }

    /**
     * @param isMute
     */
    public void setIsMute(Integer isMute) {
        this.isMute = isMute;
    }

    /**
     * @return clearStatus
     */
    public Integer getClearStatus() {
        return clearStatus;
    }

    /**
     * @param clearStatus
     */
    public void setClearStatus(Integer clearStatus) {
        this.clearStatus = clearStatus;
    }

    /**
     * @return clearTimeAt
     */
    public Long getClearTimeAt() {
        return clearTimeAt;
    }

    /**
     * @param clearTimeAt
     */
    public void setClearTimeAt(Long clearTimeAt) {
        this.clearTimeAt = clearTimeAt;
    }

    /**
     * @return memberProtection
     */
    public Integer getMemberProtection() {
        return memberProtection;
    }

    /**
     * @param memberProtection
     */
    public void setMemberProtection(Integer memberProtection) {
        this.memberProtection = memberProtection;
    }

    /**
     * @return copiedTime
     */
    public Long getCopiedTime() {
        return copiedTime;
    }

    /**
     * @param copiedTime
     */
    public void setCopiedTime(Long copiedTime) {
        this.copiedTime = copiedTime;
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

    /**
     * @return deletedAt
     */
    public Date getDeletedAt() {
        return deletedAt;
    }

    /**
     * @param deletedAt
     */
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     * @return bulletin
     */
    public String getBulletin() {
        return bulletin;
    }

    /**
     * @param bulletin
     */
    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }
}