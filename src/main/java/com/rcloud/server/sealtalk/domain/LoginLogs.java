package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "login_logs")
public class LoginLogs implements Serializable {
    @Id
    private Integer id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "ipAddress")
    private Integer ipAddress;

    private String os;

    @Column(name = "osVersion")
    private String osVersion;

    private String carrier;

    private String device;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "userAgent")
    private String userAgent;

    @Column(name = "createdAt")
    private Date createdAt;

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
     * @return ipAddress
     */
    public Integer getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress
     */
    public void setIpAddress(Integer ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return carrier
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * @return device
     */
    public String getDevice() {
        return device;
    }

    /**
     * @param device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * @return manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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
}