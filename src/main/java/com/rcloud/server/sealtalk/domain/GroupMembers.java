package com.rcloud.server.sealtalk.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "group_members")
public class GroupMembers implements Serializable {
    @Id
    private Integer id;

    @Column(name = "groupId")
    private Integer groupId;

    @Column(name = "memberId")
    private Integer memberId;

    @Column(name = "displayName")
    private String displayName;

    private Integer role;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @Column(name = "groupNickname")
    private String groupNickname;

    private String region;

    private String phone;

    @Column(name = "WeChat")
    private String weChat;

    @Column(name = "Alipay")
    private String alipay;

    @Column(name = "memberDesc")
    private String memberDesc;

    private Long timestamp;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Transient
    private Groups groups;

    private static final long serialVersionUID = 1L;

}