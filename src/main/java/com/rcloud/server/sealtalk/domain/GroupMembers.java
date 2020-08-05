package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "group_members")
public class GroupMembers implements Serializable {
    @Id
    private Integer id;

    private Integer groupId;

    private Integer memberId;

    private String displayName;

    private Integer role;

    private Boolean isDeleted;

    private String groupNickname;

    private String region;

    private String phone;

    @Column(name = "WeChat")
    private String weChat;

    @Column(name = "Alipay")
    private String alipay;

    private String memberDesc;

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
     * @return memberId
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * @param memberId
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return role
     */
    public Integer getRole() {
        return role;
    }

    /**
     * @param role
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * @return isDeleted
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return groupNickname
     */
    public String getGroupNickname() {
        return groupNickname;
    }

    /**
     * @param groupNickname
     */
    public void setGroupNickname(String groupNickname) {
        this.groupNickname = groupNickname;
    }

    /**
     * @return region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return WeChat
     */
    public String getWeChat() {
        return weChat;
    }

    /**
     * @param weChat
     */
    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    /**
     * @return Alipay
     */
    public String getAlipay() {
        return alipay;
    }

    /**
     * @param alipay
     */
    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    /**
     * @return memberDesc
     */
    public String getMemberDesc() {
        return memberDesc;
    }

    /**
     * @param memberDesc
     */
    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
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