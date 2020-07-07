DROP TABLE IF EXISTS `blacklists`;
CREATE TABLE `blacklists`
(
    `id`        int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `userId`    int(10) unsigned    NOT NULL,
    `friendId`  int(10) unsigned    NOT NULL,
    `status`    tinyint(1)          NOT NULL,
    `timestamp` bigint(20) unsigned NOT NULL DEFAULT '0',
    `createdAt` datetime            NOT NULL,
    `updatedAt` datetime            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `blacklists_user_id_friend_id` (`userId`, `friendId`),
    KEY `blacklists_user_id_timestamp` (`userId`, `timestamp`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `data_versions`;
CREATE TABLE `data_versions`
(
    `userId`             int(10) unsigned    NOT NULL,
    `userVersion`        bigint(20) unsigned NOT NULL DEFAULT '0',
    `blacklistVersion`   bigint(20) unsigned NOT NULL DEFAULT '0',
    `friendshipVersion`  bigint(20) unsigned NOT NULL DEFAULT '0',
    `groupVersion`       bigint(20) unsigned NOT NULL DEFAULT '0',
    `groupMemberVersion` bigint(20) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`userId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships`
(
    `id`          int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `userId`      int(10) unsigned    NOT NULL,
    `friendId`    int(10) unsigned    NOT NULL,
    `displayName` varchar(32)         NOT NULL DEFAULT '',
    `message`     varchar(64)         NOT NULL,
    `status`      int(10) unsigned    NOT NULL,
    `region`      varchar(32)                  DEFAULT '',
    `phone`       varchar(32)                  DEFAULT '',
    `description` varchar(500)                 DEFAULT '',
    `imageUri`    varchar(256)                 DEFAULT '',
    `timestamp`   bigint(20) unsigned NOT NULL DEFAULT '0',
    `createdAt`   datetime            NOT NULL,
    `updatedAt`   datetime            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `friendships_user_id_friend_id` (`userId`, `friendId`),
    KEY `friendships_user_id_timestamp` (`userId`, `timestamp`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_bulletins`;
CREATE TABLE `group_bulletins`
(
    `id`        int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `groupId`   int(10) unsigned    NOT NULL,
    `content`   text,
    `timestamp` bigint(20) unsigned NOT NULL DEFAULT '0',
    `createdAt` datetime            NOT NULL,
    `updatedAt` datetime            NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_exited_lists`;
CREATE TABLE `group_exited_lists`
(
    `id`              int(10) unsigned NOT NULL AUTO_INCREMENT,
    `groupId`         int(10) unsigned NOT NULL,
    `quitUserId`      int(10) unsigned NOT NULL,
    `quitNickname`    varchar(32)      NOT NULL,
    `quitPortraitUri` varchar(256)     NOT NULL DEFAULT '',
    `quitReason`      int(10) unsigned NOT NULL,
    `quitTime`        bigint(20)       NOT NULL,
    `operatorId`      int(10) unsigned          DEFAULT NULL,
    `operatorName`    varchar(32)               DEFAULT NULL,
    `createdAt`       datetime         NOT NULL,
    `updatedAt`       datetime         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `group_exited_lists_id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_favs`;
CREATE TABLE `group_favs`
(
    `id`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `userId`    int(10) unsigned NOT NULL,
    `groupId`   int(10) unsigned NOT NULL,
    `createdAt` datetime         NOT NULL,
    `updatedAt` datetime         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `groupfavindex` (`userId`, `groupId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_members`;
CREATE TABLE `group_members`
(
    `id`            int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `groupId`       int(10) unsigned    NOT NULL,
    `memberId`      int(10) unsigned    NOT NULL,
    `displayName`   varchar(32)         NOT NULL DEFAULT '',
    `role`          int(10) unsigned    NOT NULL,
    `isDeleted`     tinyint(1)          NOT NULL DEFAULT '0',
    `groupNickname` varchar(32)         NOT NULL DEFAULT '',
    `region`        varchar(32)                  DEFAULT '',
    `phone`         varchar(32)                  DEFAULT '',
    `WeChat`        varchar(32)                  DEFAULT '',
    `Alipay`        varchar(32)                  DEFAULT '',
    `memberDesc`    varchar(800)                 DEFAULT '',
    `timestamp`     bigint(20) unsigned NOT NULL DEFAULT '0',
    `createdAt`     datetime            NOT NULL,
    `updatedAt`     datetime            NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `group_members_group_id_member_id_is_deleted` (`groupId`, `memberId`, `isDeleted`),
    KEY `group_members_member_id_timestamp` (`memberId`, `timestamp`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups`
(
    `id`               int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `name`             varchar(32)         NOT NULL,
    `portraitUri`      varchar(256)        NOT NULL DEFAULT '',
    `memberCount`      int(10) unsigned    NOT NULL DEFAULT '0',
    `maxMemberCount`   int(10) unsigned    NOT NULL DEFAULT '500',
    `creatorId`        int(10) unsigned    NOT NULL,
    `bulletin`         text,
    `certiStatus`      int(10) unsigned    NOT NULL DEFAULT '1',
    `isMute`           int(10) unsigned    NOT NULL DEFAULT '0',
    `clearStatus`      int(10) unsigned    NOT NULL DEFAULT '0',
    `clearTimeAt`      bigint(20) unsigned NOT NULL DEFAULT '0',
    `memberProtection` int(10) unsigned    NOT NULL DEFAULT '0',
    `copiedTime`       bigint(20) unsigned NOT NULL DEFAULT '0',
    `timestamp`        bigint(20) unsigned NOT NULL DEFAULT '0',
    `createdAt`        datetime            NOT NULL,
    `updatedAt`        datetime            NOT NULL,
    `deletedAt`        datetime                     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `groups_id_timestamp` (`id`, `timestamp`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`             int(10) unsigned NOT NULL AUTO_INCREMENT,
    `region`         varchar(5)       NOT NULL,
    `phone`          varchar(11)      NOT NULL,
    `nickname`       varchar(32)      NOT NULL,
    `portraitUri`    varchar(256)     NOT NULL DEFAULT '',
    `passwordHash`   char(40)         NOT NULL,
    `passwordSalt`   char(4)          NOT NULL,
    `rongCloudToken` varchar(256)     NOT NULL DEFAULT '',
    `gender`         varchar(32)      NOT NULL DEFAULT 'male',
    `stAccount`      varchar(32)      NOT NULL DEFAULT '',
    `phoneVerify`    int(10) unsigned NOT NULL DEFAULT '1',
    `stSearchVerify` int(10) unsigned NOT NULL DEFAULT '1',
    `friVerify`      int(10) unsigned NOT NULL DEFAULT '1',
    `groupVerify`    int(10) unsigned NOT NULL DEFAULT '1',
    `pokeStatus`     int(10) unsigned NOT NULL DEFAULT '1',
    `groupCount`     int(10) unsigned NOT NULL DEFAULT '0',
    `timestamp`      bigint(20)       NOT NULL DEFAULT '0',
    `createdAt`      datetime         NOT NULL,
    `updatedAt`      datetime         NOT NULL,
    `deletedAt`      datetime                  DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `users_region_phone` (`region`, `phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_receivers`;
CREATE TABLE `group_receivers`
(
    `id`               int(10) unsigned NOT NULL AUTO_INCREMENT,
    `userId`           int(10) unsigned NOT NULL,
    `groupId`          int(10) unsigned          DEFAULT NULL,
    `groupName`        varchar(32)      NOT NULL DEFAULT '',
    `groupPortraitUri` varchar(256)     NOT NULL DEFAULT '',
    `requesterId`      int(10) unsigned          DEFAULT NULL,
    `receiverId`       int(10) unsigned          DEFAULT NULL,
    `type`             int(10) unsigned NOT NULL,
    `status`           int(10) unsigned NOT NULL,
    `deletedUsers`     varchar(256)     NOT NULL DEFAULT '',
    `isRead`           int(10) unsigned NOT NULL,
    `joinInfo`         varchar(256)     NOT NULL DEFAULT '',
    `timestamp`        bigint(20)       NOT NULL DEFAULT '0',
    `createdAt`        datetime         NOT NULL,
    `updatedAt`        datetime         NOT NULL,
    PRIMARY KEY (`id`),
    KEY `groupId` (`groupId`),
    KEY `requesterId` (`requesterId`),
    KEY `receiverId` (`receiverId`),
    CONSTRAINT `group_receivers_ibfk_1` FOREIGN KEY (`groupId`) REFERENCES `groups` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `group_receivers_ibfk_2` FOREIGN KEY (`requesterId`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `group_receivers_ibfk_3` FOREIGN KEY (`receiverId`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `group_syncs`;
CREATE TABLE `group_syncs`
(
    `groupId`    int(10) unsigned NOT NULL,
    `syncInfo`   tinyint(1)       NOT NULL DEFAULT '0',
    `syncMember` tinyint(1)       NOT NULL DEFAULT '0',
    `dismiss`    tinyint(1)       NOT NULL DEFAULT '0',
    PRIMARY KEY (`groupId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `login_logs`;
CREATE TABLE `login_logs`
(
    `id`           int(10) unsigned NOT NULL AUTO_INCREMENT,
    `userId`       int(10) unsigned NOT NULL,
    `ipAddress`    int(10) unsigned NOT NULL,
    `os`           varchar(64)      NOT NULL,
    `osVersion`    varchar(64)      NOT NULL,
    `carrier`      varchar(64)      NOT NULL,
    `device`       varchar(64)  DEFAULT NULL,
    `manufacturer` varchar(64)  DEFAULT NULL,
    `userAgent`    varchar(256) DEFAULT NULL,
    `createdAt`    datetime         NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `screen_statuses`;
CREATE TABLE `screen_statuses`
(
    `id`               int(10) unsigned NOT NULL AUTO_INCREMENT,
    `operateId`        char(40)         NOT NULL,
    `conversationType` int(10) unsigned NOT NULL,
    `status`           int(10) unsigned NOT NULL,
    `createdAt`        datetime         NOT NULL,
    `updatedAt`        datetime         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `screen_statuses_operate_id` (`operateId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `verification_codes`;
CREATE TABLE `verification_codes`
(
    `id`        int(10) unsigned                             NOT NULL AUTO_INCREMENT,
    `region`    varchar(5)                                   NOT NULL,
    `phone`     varchar(11)                                  NOT NULL,
    `sessionId` varchar(32)                                  NOT NULL,
    `token`     char(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
    `createdAt` datetime                                     NOT NULL,
    `updatedAt` datetime                                     NOT NULL,
    PRIMARY KEY (`id`, `region`, `phone`),
    UNIQUE KEY `token` (`token`),
    UNIQUE KEY `verification_codes_region_phone` (`region`, `phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `verification_violations`;
CREATE TABLE `verification_violations`
(
    `ip`    varchar(64) NOT NULL,
    `time`  datetime    NOT NULL,
    `count` int(10) unsigned DEFAULT NULL,
    PRIMARY KEY (`ip`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;