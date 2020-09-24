package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "data_versions")
public class DataVersions implements Serializable {

    @Id
    @Column(name = "userId")
    private Integer userId;

    @Column(name = "userVersion")
    private Long userVersion;

    @Column(name = "blacklistVersion")
    private Long blacklistVersion;

    @Column(name = "friendshipVersion")
    private Long friendshipVersion;

    @Column(name = "groupVersion")
    private Long groupVersion;

    @Column(name = "groupMemberVersion")
    private Long groupMemberVersion;

    private static final long serialVersionUID = 1L;

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
     * @return userVersion
     */
    public Long getUserVersion() {
        return userVersion;
    }

    /**
     * @param userVersion
     */
    public void setUserVersion(Long userVersion) {
        this.userVersion = userVersion;
    }

    /**
     * @return blacklistVersion
     */
    public Long getBlacklistVersion() {
        return blacklistVersion;
    }

    /**
     * @param blacklistVersion
     */
    public void setBlacklistVersion(Long blacklistVersion) {
        this.blacklistVersion = blacklistVersion;
    }

    /**
     * @return friendshipVersion
     */
    public Long getFriendshipVersion() {
        return friendshipVersion;
    }

    /**
     * @param friendshipVersion
     */
    public void setFriendshipVersion(Long friendshipVersion) {
        this.friendshipVersion = friendshipVersion;
    }

    /**
     * @return groupVersion
     */
    public Long getGroupVersion() {
        return groupVersion;
    }

    /**
     * @param groupVersion
     */
    public void setGroupVersion(Long groupVersion) {
        this.groupVersion = groupVersion;
    }

    /**
     * @return groupMemberVersion
     */
    public Long getGroupMemberVersion() {
        return groupMemberVersion;
    }

    /**
     * @param groupMemberVersion
     */
    public void setGroupMemberVersion(Long groupMemberVersion) {
        this.groupMemberVersion = groupMemberVersion;
    }
}