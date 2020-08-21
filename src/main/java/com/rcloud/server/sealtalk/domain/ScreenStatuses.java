package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "screen_statuses")
public class ScreenStatuses implements Serializable {
    @Id
    private Integer id;

    @Column(name = "operateId")
    private String operateId;

    @Column(name = "conversationType")
    private Integer conversationType;

    private Integer status;

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
     * @return operateId
     */
    public String getOperateId() {
        return operateId;
    }

    /**
     * @param operateId
     */
    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    /**
     * @return conversationType
     */
    public Integer getConversationType() {
        return conversationType;
    }

    /**
     * @param conversationType
     */
    public void setConversationType(Integer conversationType) {
        this.conversationType = conversationType;
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