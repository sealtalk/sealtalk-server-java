package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "group_syncs")
public class GroupSyncs implements Serializable {
    @Id
    private Integer groupId;

    //TODO
    private Boolean syncInfo;

    private Boolean syncMember;

    private Boolean dismiss;

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

    /**
     * @return syncInfo
     */
    public Boolean getSyncInfo() {
        return syncInfo;
    }

    /**
     * @param syncInfo
     */
    public void setSyncInfo(Boolean syncInfo) {
        this.syncInfo = syncInfo;
    }

    /**
     * @return syncMember
     */
    public Boolean getSyncMember() {
        return syncMember;
    }

    /**
     * @param syncMember
     */
    public void setSyncMember(Boolean syncMember) {
        this.syncMember = syncMember;
    }

    /**
     * @return dismiss
     */
    public Boolean getDismiss() {
        return dismiss;
    }

    /**
     * @param dismiss
     */
    public void setDismiss(Boolean dismiss) {
        this.dismiss = dismiss;
    }
}