package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "users")
public class Users implements Serializable {

    //允许直接添加至群聊： 0 不允许 1 允许
    public static final Integer GROUP_VERIFY_NO_NEED = 1;
    public static final Integer GROUP_VERIFY_NEED = 0;

    //允许 通过手机号搜索到我： 0 不允许 1允许
    public static final Integer PHONE_VERIFY_NO_NEED = 1;
    public static final Integer PHONE_VERIFY_NEED = 0;
    //允许 SealTalk 号搜索到我： 0 不允许 1允许
    public static final Integer ST_SEARCH_VERIFY_NO_NEED = 1;
    public static final Integer ST_SEARCH_VERIFY_NEED = 0;




    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String region;

    private String phone;

    private String nickname;

    @Column(name="portraitUri")
    private String portraitUri;

    @Column(name="passwordHash")
    private String passwordHash;

    @Column(name="passwordSalt")
    private String passwordSalt;

    @Column(name="rongCloudToken")
    private String rongCloudToken;

    private String gender;

    @Column(name="stAccount")
    private String stAccount;

    @Column(name="phoneVerify")
    private Integer phoneVerify;

    @Column(name="stSearchVerify")
    private Integer stSearchVerify;

    @Column(name="friVerify")
    private Integer friVerify;

    @Column(name="groupVerify")
    private Integer groupVerify;

    @Column(name="pokeStatus")
    private Integer pokeStatus;

    @Column(name="groupCount")
    private Integer groupCount;

    private Long timestamp;

    @Column(name="createdAt")
    private Date createdAt;

    @Column(name="updatedAt")
    private Date updatedAt;

    @Column(name="deletedAt")
    private Date deletedAt;

    @Transient
    private Groups groups;

    public Groups getGroups() {
        return groups;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

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
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
     * @return passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @param passwordHash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * @return passwordSalt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * @param passwordSalt
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * @return rongCloudToken
     */
    public String getRongCloudToken() {
        return rongCloudToken;
    }

    /**
     * @param rongCloudToken
     */
    public void setRongCloudToken(String rongCloudToken) {
        this.rongCloudToken = rongCloudToken;
    }

    /**
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return stAccount
     */
    public String getStAccount() {
        return stAccount;
    }

    /**
     * @param stAccount
     */
    public void setStAccount(String stAccount) {
        this.stAccount = stAccount;
    }

    /**
     * @return phoneVerify
     */
    public Integer getPhoneVerify() {
        return phoneVerify;
    }

    /**
     * @param phoneVerify
     */
    public void setPhoneVerify(Integer phoneVerify) {
        this.phoneVerify = phoneVerify;
    }

    /**
     * @return stSearchVerify
     */
    public Integer getStSearchVerify() {
        return stSearchVerify;
    }

    /**
     * @param stSearchVerify
     */
    public void setStSearchVerify(Integer stSearchVerify) {
        this.stSearchVerify = stSearchVerify;
    }

    /**
     * @return friVerify
     */
    public Integer getFriVerify() {
        return friVerify;
    }

    /**
     * @param friVerify
     */
    public void setFriVerify(Integer friVerify) {
        this.friVerify = friVerify;
    }

    /**
     * @return groupVerify
     */
    public Integer getGroupVerify() {
        return groupVerify;
    }

    /**
     * @param groupVerify
     */
    public void setGroupVerify(Integer groupVerify) {
        this.groupVerify = groupVerify;
    }

    /**
     * @return pokeStatus
     */
    public Integer getPokeStatus() {
        return pokeStatus;
    }

    /**
     * @param pokeStatus
     */
    public void setPokeStatus(Integer pokeStatus) {
        this.pokeStatus = pokeStatus;
    }

    /**
     * @return groupCount
     */
    public Integer getGroupCount() {
        return groupCount;
    }

    /**
     * @param groupCount
     */
    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
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

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", passwordSalt='" + passwordSalt + '\'' +
                ", rongCloudToken='" + rongCloudToken + '\'' +
                ", gender='" + gender + '\'' +
                ", stAccount='" + stAccount + '\'' +
                ", phoneVerify=" + phoneVerify +
                ", stSearchVerify=" + stSearchVerify +
                ", friVerify=" + friVerify +
                ", groupVerify=" + groupVerify +
                ", pokeStatus=" + pokeStatus +
                ", groupCount=" + groupCount +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}