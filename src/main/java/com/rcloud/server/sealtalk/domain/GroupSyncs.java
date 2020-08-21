package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "group_syncs")
public class GroupSyncs implements Serializable {

    public static final Integer VALID = 1;
    public static final Integer INVALID = 0;


    @Id
    @Column(name = "groupId")
    private Integer groupId;

    @Column(name = "syncInfo")
    private Integer syncInfo;

    @Column(name = "syncMember")
    private Integer syncMember;

    private Integer dismiss;

    private static final long serialVersionUID = 1L;

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

    public Integer getSyncInfo() {
        return syncInfo;
    }

    public void setSyncInfo(Integer syncInfo) {
        this.syncInfo = syncInfo;
    }

    public Integer getSyncMember() {
        return syncMember;
    }

    public void setSyncMember(Integer syncMember) {
        this.syncMember = syncMember;
    }

    public Integer getDismiss() {
        return dismiss;
    }

    public void setDismiss(Integer dismiss) {
        this.dismiss = dismiss;
    }
}