package com.rcloud.server.sealtalk.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "groups")
public class Groups implements Serializable {
    @Id
    private Integer id;

    private String name;

    @Column(name = "portraitUri")
    private String portraitUri;

    @Column(name = "memberCount")
    private Integer memberCount;

    @Column(name = "maxMemberCount")
    private Integer maxMemberCount;

    @Column(name = "creatorId")
    private Integer creatorId;

    @Column(name = "certiStatus")
    private Integer certiStatus;

    @Column(name = "isMute")
    private Integer isMute;

    @Column(name = "clearStatus")
    private Integer clearStatus;

    @Column(name = "clearTimeAt")
    private Long clearTimeAt;

    @Column(name = "memberProtection")
    private Integer memberProtection;

    @Column(name = "copiedTime")
    private Long copiedTime;

    private Long timestamp;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "deletedAt")
    private Date deletedAt;

    @Column(name = "bulletin")
    private String bulletin;

    private static final long serialVersionUID = 1L;

}