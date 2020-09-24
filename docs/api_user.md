### 用户相关接口

| 接口地址 | 说明 |
|---------|-----|
| [/user/send_code](#post-usersend_code) | 向手机发送验证码 |
| [/user/send_code_yp](#post-usersend_code_yp) | 向手机发送验证码(云片服务) |
| [/user/verify_code](#post-userverify_code) | 验证验证码 |
| [/user/verify_code_yp](#post-userverify_code_yp) | 验证验证码(云片服务) |
| [/user/regionlist](#get-userregionlist) | 获取所有区域信息 |
| [/user/check_phone_available](#post-usercheck_phone_available) | 检查手机号是否可以注册 |
| [/user/register](#post-userregister) | 注册新用户 |
| [/user/login](#post-userlogin) | 用户登录 |
| [/user/logout](#post-userlogout) | 当前用户注销 |
| [/user/reset_password](#post-userreset_password) | 通过手机验证码设置新密码 |
| [/user/change_password](#post-userchange_password) | 当前登录用户通过旧密码设置新密码 |
| [/user/set_nickname](#post-userset_nickname) | 设置当前用户的昵称 |
| [/user/set_portrait_uri](#post-userset_portrait_uri) | 设置当前用户头像地址 |
| [/user/blacklist](#get-userblacklist) | 获取当前用户黑名单列表 |
| [/user/add_to_blacklist](#post-useradd_to_blacklist) | 将好友加入黑名单 |
| [/user/remove_from_blacklist](#post-userremove_from_blacklist) | 将好友从黑名单中移除 |
| [/user/get_token](#post-userget_token) | 获取融云 Token |
| [/user/get_image_token](#post-userget_image_token) | 获取云存储所用 Token |
| [/user/get_sms_img_code](#post-userget_sms_img_code) | 获取短信图形验证码 |
| [/user/groups](#post-usergroups) | 获取当前用户所属群组列表 |
| [/user/sync/:version](#post-usersyncversion) | 同步用户的好友、黑名单、群组、群组成员数据 |
| [/user/find/:region/:phone](#post-userfindregionphone) | 根据手机号查找用户信息 |
| [/user/:id](#post-userid) | 获取用户信息 |
| [/user/favgroups](#get-userfavgroups) | 获取通讯录群组 |
| [/user/set_st_account](#post-userset_st_account) | 设置 SealTalk 号 |
| [/user/set_gender](#post-userset_gender) | 设置性别 |
| [/user/set_privacy](#post-userset_privacy) | 个人隐私设置 |
| [/user/get_privacy](#get-userget_privacy) | 获取个人隐私设置 |
| [/user/set_poke](#post-usersetpoke) | 设置接收戳一下消息状态 |
| [/user/get_poke](#get-usergetpoke) | 获取接收戳一下消息状态 |

## API 说明

请注意文档中 `返回码` 和 HTTP Status Code 之间的区别，`返回码`是 HTTP Status Code 为 `200` 时，返回的 JSON 结果集中 `code` 的值，`code` 值正常返回时，也是 `200` 请注意区分，避免混淆。

### POST /user/send_code

向手机发送验证码。

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678
}
```

* region: 国际电话区号
* phone: 手机号

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 发送成功
* 5000: 发送失败，超过频率限制

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/send_code_yp

向手机发送验证码(使用云片服务)

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678
}
```

* region: 国际电话区号
* phone: 手机号

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 发送成功
* 5000: 发送失败，超过频率限制

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

其他错误码详见: [云片短信错误码汇总](./error_sendcode_yp.md)

### POST /user/verify_code

验证验证码。

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678,
  "code": '1234'
}
```

* region: 国际电话区号
* phone: 手机号
* code: 验证码，由 /user/send_code 方法发送到手机上

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "verification_token": "75dd0f90-9b0d-11e5-803f-59b82644bc50"
  }
}
```

* verification_token: 校验 Token

返回码说明：

* 200: 验证成功
* 1000: 验证码错误
* 2000: 验证码过期

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/verify_code_yp

验证验证码。

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678,
  "code": '1234'
}
```

* region: 国际电话区号
* phone: 手机号
* code: 验证码，由 /user/send_code_yp 方法发送到手机上

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "verification_token": "75dd0f90-9b0d-11e5-803f-59b82644bc50"
  }
}
```

* verification_token: 校验 Token

返回码说明：

* 200: 验证成功
* 1000: 验证码错误
* 2000: 验证码过期

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### GET /user/regionlist

获取所有区域信息。

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": [
    {
      "region": "1",
      "locale": {
        "en": "Canada",
        "zh": "加拿大"
      }
    },
    {
      "region": "7",
      "locale": {
        "en": "Kazakhstan",
        "zh": "哈萨克"
      }
    }
  ]
}
```

* region: 区号
* locale: 多语言, 目前只包含中文和英文

返回码说明：

* 200: 验证成功
* 1000: 区域信息错误

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误


### POST /user/check_phone_available

检查手机号是否可以注册。

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678
}
```

* region: 国际电话区号
* phone: 手机号

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": true
}
```

返回码说明：

* 200: 请求成功

返回结果说明：

* true: 手机号可用
* false: 手机号不可用

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/register

注册新用户。

#### 请求参数

```
{
  "nickname": "Tom",
  "password": "P@ssw0rd",
  "verification_token": "75dd0f90-9b0d-11e5-803f-59b82644bc50"
}
```

* nickname: 昵称，1 到 32 个字节
* password: 密码，6 到 20 个字节，不能包含空格
* verification_token: 调用 /user/verify_code 成功后返回的 verification_token

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "id": "5Vg2Xh9f"
  }
}
```

* id: 注册的用户 Id

返回码说明：

* 200: 请求成功

返回结果说明：

* id: 注册的用户 Id

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 404: verification_token 不存在
* 500: 应用服务器内部错误

### POST /user/login

用户登录。登录成功后，会设置 Cookie，后续接口调用需要登录的权限都依赖于 Cookie。

#### 请求参数

```
{
  "region": 86,
  "phone": 13912345678,
  "password": "P@ssw0rd"
}
```

* region: 国际电话区号
* phone: 手机号
* password: 密码，6 到 20 个字节，不能包含空格

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "id": "5Vg2Xh9f",
    "token": "C4nEgo1TK0Ly6zUr/+Hqqu/XQOlLIWwcquFNlNhLydOQwZlSzscUQQfhEU6nFWJ+yPKQhMU6qP5XXBgOWA1AhckFbQ/t+nm4"
  }
}
```

* id: 登录用户 Id
* token: 融云 Token

返回码说明：

* 200: 请求成功
* 1000: 错误的手机号或者密码

返回结果说明：

* id: 登录的用户 Id

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/logout

当前用户注销。

#### 前置条件

需要登录才能访问。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

### POST /user/reset_password

通过手机验证码设置新密码。

#### 请求参数

```
{
  "password": "P@ssw0rd",
  "verification_token": "75dd0f90-9b0d-11e5-803f-59b82644bc50"
}
```

* password: 密码，6 到 20 个字节，不能包含空格
* verification_token: 调用 /user/verify_code 成功后返回的 verification_token

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/change_password

当前登录用户通过旧密码设置新密码。

#### 前置条件

需要登录才能访问。

#### 请求参数

```
{
  "oldPassword": "P@ssw0rdOld",
  "newPassword": "P@ssw0rdNew"
}
```

* oldPassword: 旧密码，6 到 20 个字节，不能包含空格
* newPassword: 新密码，6 到 20 个字节，不能包含空格

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/set_nickname

设置当前用户的昵称

#### 前置条件

需要登录才能访问。

#### 请求参数

```
{
  "nickname": "Tom"
}
```

* nickname: 当前用户的昵称

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/set_portrait_uri

设置当前用户头像地址。

#### 前置条件

需要登录才能访问。

#### 请求参数

```
{
  "portraitUri": "http://test.com/user/abc123.jpg"
}
```

* portraitUri: 当前用户的头像地址

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### GET /user/blacklist

获取当前用户黑名单列表。

#### 前置条件

需要登录才能访问。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
    "result": [{
      "user": {
        "id": "42e6LfSgN",
        "nickname": "ceshi29",
        "portraitUri": "",
        "gender": "male",
        "stAccount": "",
        "phone": "18713578062",
        "updatedAt": "2019-01-04T10:41:52.000Z",
        "updatedTime": 1546598512000
      }
    }, {
      "user": {
        "id": "NyKWhpEUl",
        "nickname": "ceshi299",
        "portraitUri": "http://7xogjk.com1.z0.glb.clouddn.com/FnFGwIuINAbC49zoLSY8L_swdk2B",
        "gender": "male",
        "stAccount": "",
        "phone": "18600650648",
        "updatedAt": "2019-09-04T12:54:40.000Z",
        "updatedTime": 1567601680000
      }
    }]
}
```

* id: 用户 Id
* nickname: 用户昵称
* portraitUri: 用户头像地址
* updatedAt: 黑名单更新时间

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 500: 应用服务器内部错误

### POST /user/add_to_blacklist

将好友加入黑名单。

#### 前置条件

需要登录才能访问。

#### 请求参数

```
{
  "friendId": "d9ewr89j238"
}
```

* friendId: 要加入黑名单的好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/remove_from_blacklist

将好友从黑名单中移除。

#### 前置条件

需要登录才能访问。

#### 请求参数

```
{
  "friendId": "d9ewr89j238"
}
```

* friendId: 要移除黑名单的好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/get_token

获取融云 Token。

#### 前置条件

需要登录才能访问。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 500: 应用服务器内部错误

### POST /user/get_image_token

获取云存储所用 Token。

#### 前置条件

需要登录才能访问。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "target": "qiniu",
    "domain": "a.qiniu.com",
    "token": "fsd89feuio3iweoifds"
  }
}
```

* target: 云存储类型
* domain: 云存储图片地址域名
* token: 云存储 Token

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 500: 应用服务器内部错误

### POST /user/get_sms_img_code

获取短信图形验证码。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "url": "http://test.com/code.png",
    "verifyId": "fsd89feuio3iweoifds"
  }
}
```

* url: 验证码图片地址
* verifyId: 图形验证码校验 Id

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 500: 应用服务器内部错误

### POST /user/groups

获取当前用户所属群组列表。

#### 前置条件

需要登录才能访问。

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": [
    {
      "id": "sdf9sd0df98",
      "name": "Team 1",
      "portraitUri": "http://test.com/group/abc123.jpg",
      "creatorId": "fgh89wefd9"
      "memberCount": 5,
      "isMute": 1, // 0 关闭全员禁言、 1 开启全员禁言
      "certiStatus": 0 // 0 关闭群认证、 1 开启群认证
    },
    {
      "id": "fgh809fg098",
      "name": "Team 2",
      "portraitUri": "http://test.com/group/abc234.jpg",
      "creatorId": "kl234klj234"
      "memberCount": 8,
      "isMute": 1, // 0 关闭全员禁言、 1 开启全员禁言
      "certiStatus": 0 // 0 关闭群认证、 1 开启群认证
    }
  ]
}
```

* id: 用户 Id
* name: 群组名称
* portraitUri: 群组头像地址
* creatorId: 群组创建者 Id
* memberCount: 群组成员数

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 500: 应用服务器内部错误

### POST /user/sync/:version

同步用户的好友、黑名单、群组、群组成员数据。

参见 [客户端数据同步策略说明](#客户端数据同步策略说明)

#### 前置条件

需要登录才能访问。

#### 请求参数

* version: 请求的时间戳（版本号）

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result": {
    "version": 1234567894,
    "user": {
      "id": "sdf9sd0df98",
      "nickname": "Tom",
      "portraitUri": "http://test.com/user/abc123.jpg",
      "timestamp": 1234567891
    },
    "blacklist": [
      {
        "friendId": "sdf9sd0df98",
        "status": true,
        "timestamp": 1234567891
      }
    ],
    "friends": [
      {
        "friendId": "sdf9sd0df98",
        "displayName": "Jerry",
        "status": 20,
        "timestamp": 1234567892
      }
    ],
    "groups": [
      {
        "displayName": "Ironman",
        "role": 1,
        "isDeleted": true,
        "group": {
          "id": "sdf9sd0df98",
          "name": "Team 1",
          "portraitUri": "http://test.com/group/abc123.jpg",
          "timestamp": 1234567893
        }
      }
    ],
    "group_members": [
      {
        "groupId": "cvx989vxc9",
        "memberId": "sdf9sd0df98",
        "displayName": "Ironman",
        "role": 1,
        "isDeleted": true,
        "timestamp": 1234567893,
        "user": {
          "nickname": "Tom",
          "portraitUri": "http://test.com/user/abc123.jpg"
        }
      }
    ]
  }
}
```

* version:

* user.id: 用户 Id
* user.name: 用户名称
* user.portraitUri: 用户头像地址
* user.timestamp: 用户信息时间戳（版本号）

* blacklist.friendId: 好友 Id
* blacklist.status: 黑名单状态
* blacklist.timestamp: 黑名单信息时间戳（版本号）

* friends.friendId: 好友 Id
* friends.displayName: 好友显示名称
* friends.status: 好友状态
* friends.timestamp: 好友信息时间戳（版本号）

* groups.displayName: 在群组中的显示名称
* groups.role: 在群组中的角色
* groups.isDeleted: 是否被删除
* groups.group.id: 群组 Id
* groups.group.name: 群组名称
* groups.group.portraitUri: 群组头像地址
* groups.group.timestamp: 群组信息时间戳（版本号）

* group_members.groupId: 群组 Id
* group_members.memberId: 群组成员 Id
* group_members.displayName: 群组成员显示名称
* group_members.role: 群组成员角色
* group_members.isDeleted: 是否被删除
* group_members.timestamp: 群组成员信息时间戳（版本号）
* group_members.group.id: 群组 Id
* group_members.group.name: 群组名称
* group_members.group.portraitUri: 群组头像地址

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 500: 应用服务器内部错误

### POST /user/find/:region/:phone

根据手机号查找用户信息。

#### 前置条件

需要登录才能访问。

#### 请求参数

* region: 国际电话区号
* phone: 手机号

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result":{
    "id": "sdf9sd0df98",
    "nickname": "Tom",
    "portraitUri": "http://test.com/user/abc123.jpg"
  }
}
```

* id: 用户 Id
* nickname: 用户昵称
* portraitUri: 用户头像地址

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 404: 无此用户
* 500: 应用服务器内部错误

### POST /user/:id

获取用户信息。

#### 前置条件

需要登录才能访问。

#### 请求参数

* id: 用户 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
  "code": 200,
  "result":{
    "id": "sdf9sd0df98",
    "nickname": "Tom",
    "portraitUri": "http://test.com/user/abc123.jpg", // 用户头像地址
    "gender": "male", // 性别
    "stAccount": "b323422", // SealTalk 号
    "phone": "18701029999" // 手机号
  }
}
```

* id: 用户 Id
* nickname: 用户昵称
* portraitUri: 用户头像地址

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 404: 无此用户
* 500: 应用服务器内部错误

### GET /user/favgroups

获取通讯录群组

#### 前置条件

需要登录才能访问。

#### 请求参数

* offset
* limit

`注:` 以上参数不填则为 获取通讯录下所有群组

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
    "code": 200,
    "result": {
        "list": [
            {
                "id": "kFpN4KiZn",
                "name": "ceshiqunzu",
                "portraitUri": "",
                "creatorId": "kFpN4KiZn",
                "memberCount": 2,
                "maxMemberCount": 500,
                "memberProtection": 0,
                "createdAt": "2019-06-17T10:06:26.000Z",
                "updatedAt": "2019-06-17T10:06:26.000Z",
                "updatedTime": 1560765986000,
                "createdTime": 1560765986000
            }
        ],
        "total": 4,
        "limit": "1",
        "offset": "0"
    }
}
```

返回码说明：

* 200: 请求成功

异常返回，返回的 HTTP Status Code 如下：

* 400: 错误的请求
* 404: 无此用户
* 500: 应用服务器内部错误

### POST /user/set_st_account

设置 SealTalk 号

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|stAccount|sealtalk 号|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200, // 200 设置成功  1000 SealTalk 号已存在
}

```

### POST /user/set_gender

设置性别

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|gender|性别：男性 male 女性 female|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```

### POST /user/set_privacy

个人隐私设置

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|phoneVerify|允许 通过手机号搜索到我： 0 不允许 1允许 |Number| 否|
|stSearchVerify|允许 SealTalk 号搜索到我： 0 不允许 1允许 |Number| 否|
|friVerify|加好友验证： 0 不需验证 1 需验证 |Number| 否|
|groupVerify|允许直接添加至群聊： 0 不允许 1 允许 |Number| 否|

设置哪个传哪个（可同时设置多个），传空为不设置

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```
## GET /user/get_privacy

获取个人隐私设置

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"phoneVerify": 0, //  0 不允许 1允许
	"stSearchVerify": 0, // 0 不允许 1允许
	"friVerify": 0,// 0 不需验证 1 需验证
	"groupVerify": 1,// 0 不允许 1 允许
}

```

<<<<<<< HEAD
=======
### POST /user/set_poke

设置接收戳一下消息状态

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|pokeStatus|接收戳一下消息状态： 0 不允许 1允许 |Number| 否|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```
## GET /user/get_poke

获取接收戳一下消息状态

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"pokeStatus": 0, //  0 不允许 1允许
}

```

>>>>>>> c-mobile-refactor-2.2.0
