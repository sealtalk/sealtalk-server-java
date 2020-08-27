package com.rcloud.server.sealtalk.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "verification_violations")
public class VerificationViolations implements Serializable {
    /**
     * 获取验证码的ip
     */
    @Id
    private String ip;

    /**
     * 获取验证码的时间
     */
    private Date time;

    /**
     * 获取验证码的次数
     */
    private Integer count;

    private static final long serialVersionUID = 1L;

    /**
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
    }
}