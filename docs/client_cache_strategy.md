## 客户端数据同步策略说明

通过以版本号为基础的数据同步策略，能够极大的降低客户端到服务器的请求次数和流量，提高业务性能和用户体验。注意：客户端数据同步策略并不需要强制使用。

### 本地缓存数据库设计

客户端本地建立如下一套表格作为本地数据缓存：用户表、黑名单表、好友关系表、加入的群组表、加入的群组的成员关系表，用来存储需要的数据。各表结构如下：

用户表（当前用户及其好友）：

| 字段名       |    数据类型    |    说明   |
|-------------|:-------------:|----------|
| id          | INT UNSIGNED  | 用户 Id |
| nickname    | VARCHAR(32)   | 用户的昵称 |
| portraitUri | VARCHAR(256)  | 用户的头像地址 |
| timestamp   | BIGINT        | 时间戳（版本号） |

黑名单表：

| 字段名       |    数据类型    |    说明   |
|-------------|:-------------:|----------|
| friendId    | INT UNSIGNED  | 好友 Id |
| status      | TINYINT       | 黑名单状态，参考 db.coffee 中的相关定义 |
| timestamp   | BIGINT        | 时间戳（版本号） |

好友关系表：

| 字段名       |    数据类型    |    说明   |
|-------------|:-------------:|----------|
| friendId    | INT UNSIGNED  | 好友 Id |
| displayName | VARCHAR(32)   | 好友屏显名 |
| status      | INT           | 好友关系状态，参考 db.coffee 中的相关定义 |
| timestamp   | BIGINT        | 时间戳（版本号）|

加入的群组表：

| 字段名       |    数据类型    |    说明   |
|-------------|:-------------:|----------|
| id          | INT UNSIGNED  | 群组 Id |
| name        | VARCHAR(32)   | 群组名称 |
| portraitUri | VARCHAR(256)  | 群组头像地址 |
| displayName | VARCHAR(32)   | 当前用户在群组中的屏显名 |
| role        | INT           | 当前用户在群组中的权限，参考 db.coffee |
| timestamp   | BIGINT        | 时间戳（版本号）|

加入的群组的成员关系表：

| 字段名       |    数据类型    |    说明   |
|-------------|:-------------:|----------|
| groupId     | INT UNSIGNED  | 群组成员所属群组 Id |
| memberId    | INT UNSIGNED  | 群组成员 Id |
| displayName | VARCHAR(32)   | 群组成员的屏显名 |
| role        | INT           | 群组成员的权限，参考 db.coffee |
| nickname    | VARCHAR(32)   | 群组成员的昵称 |
| portraitUri | VARCHAR(256)  | 群组成员的头像 |
| timestamp   | BIGINT        | 时间戳（版本号）|

### 同步策略

客户端本地保存一个当前版本号数据，如 `version`，用户创建时，本地值为 `0`

1、登录同步：

每次登录后，调用服务器 `GET /user/sync/:version` 接口，将本地的版本号 `version` 传递给服务端，服务端会返回 `version` 之后所有的变化数据结果集。

根据情况，将结果集的数据更新到本地缓存表中，包括插入、更新、删除（结果集中返回群组、群成员 isDeleted == true 或者黑名单 status == 0 或者好友关系 status == 30）

最后，将本地的版本号 `version` 更新为刚刚接口返回的最新版本号 `version` 即可。

2、操作同步：

群成员变化时，会收到通知消息 `GroupNotificationMessage`，通知消息中也包含时间戳（版本号）`timestamp`，可以根据通知消息中的信息，更新到本地缓存数据库中。

当进行各种操作时，请注意更新本地缓存中的数据，并更新本地各个字段的时间戳（版本号），但不要更新本地的 `version`。

### 本地缓存读取策略

采用客户端数据同步策略后，所有的用户信息、好友关系、黑名单列表、群组信息、群组成员信息，都可以直接从本地缓存中读取。

## 好友关系说明

在数据库 `friendships` 表 `status` 字段中包括如下值：

* FRIENDSHIP_REQUESTING = 10
* FRIENDSHIP_REQUESTED  = 11
* FRIENDSHIP_AGREED     = 20
* FRIENDSHIP_IGNORED    = 21
* FRIENDSHIP_DELETED    = 30

所有可能的状态组合如下：

| 对自己的状态 | 自己 | 好友 | 对好友的状态 |
|------------|:---:|:----:|------------|
| 发出了好友邀请 | 10 | 11 | 收到了好友邀请 |
| 发出了好友邀请 | 10 | 21 | 忽略了好友邀请 |
| 已是好友      | 20 | 20 | 已是好友      |
| 已是好友      | 20 | 30 | 删除了好友关系 |
| 删除了好友关系 | 30 | 30 | 删除了好友关系 |
