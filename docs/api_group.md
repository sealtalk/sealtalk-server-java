### 群组相关接口

| 接口地址 | 说明 |
|---------|-----|
| [/group/create](#post-groupcreate) | 创建群组 |
| [/group/add](#post-groupadd) | 添加群成员 |
| [/group/join](#post-groupjoin) | 加入群组 |
| [/group/kick](#post-groupkick) | 踢人 |
| [/group/quit](#post-groupquit) | 退出群组 |
| [/group/dismiss](#post-groupdismiss) | 解散群组 |
| [/group/transfer](#post-grouptransfer) | 转让群主角色 |
| [/group/set_manager](#post-groupset_manager) | 批量增加管理员 |
| [/group/remove_manager](#post-groupremove_manager) | 删除管理员接口 |
| [/group/rename](#post-grouprename) | 群组重命名 |
| [/group/fav](#post-groupfav) | 保存群组至通讯录 |
| [/group/set_bulletin](#post-groupset_bulletin) | 发布群公告 |
| [/group/get_bulletin](#get_groupget_bulletin) | 获取群公告 |
| [/group/set_portrait_uri](#post-groupset_portrait_uri) | 设置群头像 |
| [/group/set_display_name](#post-groupset_display_name) | 设置群名片 |
| [/group/:id](#get-groupid) | 获取群信息 |
| [/group/:id/members](#get-groupidmembers) | 获取群成员 |
| [/group/set_certification](#post-groupset_certification) | 设置群认证 |
| [/group/agree](#post-groupagree) | 同意加群请求 |
| [/group/notice_info](#get-groupnotice_info) | 群通知邀请信息 |
| [/group/clear_notice](#post-groupclear_notice) | 群通知邀请信息 |
| [/group/mute_all](#post-groupmute_all) | 全员禁言 |
| [/group/set_regular_clear](#post-groupset-regular-clear) | 开启/更新 清理群离线消息 |
| [/group/get_regular_clear](#post-groupgetregularclear) | 获取群定时清理状态 |
| [/group/set_member_info](#post-groupsetmemberinfo) | 设置群成员信息 |
| [/group/get_member_info](#post-groupgetmemberinfo) | 获取群成员信息 |
| [/group/copy_group](#post-groupcopygroup) | 复制群 |
| [/group/exited_list](#post-groupexitedlist) | 退群列表 |
| [/group/set_member_protection](#post-groupsetmemberprotection) | 群成员保护模式设置 |


## API 说明

### POST /group/create

创建群组

#### 请求参数

```
{
	"name": "RongCloud",
	"memberIds": ["AUj8X32w1", "ODbpJIgrL"],
	"portraitUri":"http://rongcloud-file.r"
}
```

* name: 群名称
* memberIds: 群成员 Id 列表, 包含 `创建者 Id`

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
	"result": {
		"id": "RfqHbcjes",
		"userStatus": [
			{
				"id": "uOavJZUpX",
				"status": 3 // 1 为已加入, 2 为等待管理员同意, 3 为等待被邀请者同意
			}
		]
	}
}
```

* id: 群组 Id

返回码说明：

* 200: 请求成功
* 400: 错误的请求
* 1000: 群组个数超限

### POST /group/add

添加群成员

#### 请求参数

```
{
	"groupId": "KC6kot3ID",
	"memberIds": ["52dzNbLBZ"]
}
```

* groupId: 群组 Id
* memberIds: userId 列表

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200,
	"result": [
		{
			"id": "uOavJZUpX",
			"status": 3  // 1 为已加入, 2 为等待管理员同意, 3 为等待被邀请者同意
		}
	]
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求

#### 消息说明

`注:` server 发送的为 fromUserId 为 '__group_apply__' 的单聊消息

ST:GrpApply

```
{
	data: {
		operatorNickname: '操作者昵称',
		targetGroupId: '群组 id',
		targetGroupName: '群组名',
		status: 2, // 0: 忽略、1: 同意、2: 等待
		type: 1 // 1: 待被邀请者处理、2: 待管理员处理
	},
	operatoerUserId: '操作者 id',
	operation: 'Invite'
}
```

### POST /group/join

用户加入群组

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* 

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求

### POST /group/kick

群主或群管理将群成员移出群组

#### 请求参数

```
{
	"groupId": "KC6kot3ID",
	"memberIds": ["52dzNbLBZ"]
}
```

* groupId: 群组 Id
* memberIds: userId 列表

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求
* 404: 未知群组
* 403: 当前用户无权限踢人
* 500: 服务器内部错误，无法同步数据至 RongCloud IM Server

### POST /group/quit

用户退出群组

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求
* 404: 未知群组
* 403: 当前用户不在群组中
* 500: 服务器内部错误，无法同步数据至 RongCloud IM Server

### POST /group/dismiss

解散群组

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 当前用户不是创建者
* 500: 服务器内部错误，无法同步数据至 RongCloud IM Server

### POST /group/transfer

转让群主

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	userId: "52dzNbLBZ"
}
```

* groupId: 群组 Id
* userId: 用户 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

#### 消息说明

ST:GrpNtf

```
{
	data: {
		oldAdminId: 'uOavJZUpX',
    oldAdminName: '旧群主名',
    newAdminId: 'kFpN4KiZn',
    newAdminName: '新群主名',
    timestamp: 1560825522194
	},
	operatoerUserId: kFpN4KiZn,
	operation: 'Transfer'
}
```

返回码说明：

* 200: 请求成功
* 400: 当前用户不是创建者
* 403: 不能把群主转让给自己

### POST /group/set_manager

批量设置管理员

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	memberIds: [ '52dzNbLBZ' ]
}
```

* groupId: 群组 Id
* memberIds: 设置为管理员的用户列表

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

#### 角色说明

```
群主: 0
成员: 1
管理员: 2
```

返回码说明：

* 200: 请求成功
* 400: 请求参数错误
* 401: 无设置权限
* 402: 不在群组中
* 403: 不能设置群主为管理员

### POST /group/remove_manager

批量删除管理员

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	memberIds: [ '52dzNbLBZ' ]
}
```

* groupId: 群组 Id
* memberIds: 删除管理员权限的用户列表

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 请求参数错误
* 401: 无设置权限
* 402: 不在群组中
* 403: 不能设置群主为普通成员

### POST /group/rename

群组重命名

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	name: "RongCloud"
}
```

* groupId: 群组 Id
* name: 群名称, 长度不超过 32 个字符

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 群名长度超限

### POST /group/fav

保存群组至通讯录

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 未知的群组
* 405: 群组已在通讯录中

### DELETE /group/fav

删除通讯录中的群组

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 未知的群组

### POST /group/set_bulletin

发布群公告

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	bulletin: "明天 4 点下班"
}
```

* groupId: 群组 Id
* bulletin: 群公告内容, 长度不超过 1024 个字符

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

#### 消息说明

ST:GrpNtf

```
{
	data: {
		content: "群公告内容",
		operatorId: 'kFpN4KiZn',
		operatorName: "随机号"
	},
	operatoerUserId: kFpN4KiZn,
	operation: 'Transfer'
}
```

返回码说明：

* 200: 请求成功
* 400: 非法请求，未知群组或当前不用不是群组

### GET /group/get_bulletin

获取群公告

`例如:` /group/get_bulletin?id=KC6kot3ID

#### 请求参数

```js
{
	groupId: "KC6kot3ID"
}
```

* id: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```js
{
  "code": 200,
  "result": {
    "id": 53,
    "groupId": 1,
    "content": "111请同学们好好听课哈",
    "timestamp": 1561019789780
  }
}
```

### POST /group/set_portrait_uri

设置群头像

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	portraitUri: "http://7xogjk.com1.z0.glb.clouddn.com/u0LUuhzHm1466557920584458984"
}
```

* groupId: 群组 Id
* portraitUri: 群头像地址, 长度不能超过 256 个字符

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 400: 非法请求

### POST /group/set_display_name

设置自己的群名片

#### 请求参数

```
{
	groupId: "KC6kot3ID",
	displayName: "Martin"
}
```

* groupId: 群组 Id
* displayName: 群名片

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 404: 未知群组

### GET /group/:id

获取群信息

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code": 200,
	"result": {
		"id": "KC6kot3ID",
		"name": "RongCloud",
		"portraitUri": "",
		"memberCount": 13,
		"maxMemberCount": 500,
		"creatorId": "I8cpNlo7t",
		"type": 1,
		"bulletin": "群公告",
		"bulletinTime": 1560931403360,
		"deletedAt": null,
		"isMute": 1, // 0 关闭全员禁言、 1 开启全员禁言
		"certiStatus": 0 // 0 关闭群认证、 1 开启群认证
		"memberProtection":0 // 0 关闭群成员保护模式、 1开启群成员保护模式
	}
}
```

* id: 群组 Id
* name: 群名称
* portraitUri: 群头像
* memberCount: 群人数
* maxMemberCount: 群人数上限
* creatorId: 群主 Id
* type: 类型 1 普通群 2 企业群
* bulletin: 群公告
* bulletinTime: 群公告发布时间
* deletedAt: 删除日期

返回码说明：

* 200: 请求成功
* 404: 未知群组

### GET /group/:id/members

获取群成员列表

#### 请求参数

```
{
	groupId: "KC6kot3ID"
}
```

* groupId: 群组 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
"code": 200,
"result": [
	{
		"groupNickname": "", //群成员昵称
		"role": 1,  // 1 为成员, 0 为群主, 2 为管理员
		"createdAt": "2016-11-22T03:06:13.000Z",
		"createdTime": 1560222249000,
		"updatedAt": "2016-11-22T03:06:13.000Z",
		"updatedTime": 1560222249000,
		"user": {
			"id": "xNlpDTUmw",
			"nickname": "zl01", //用户昵称
			"portraitUri": "",
			"gender": "male", // 性别
			"stAccount": "b323422", // SealTalk 号
			"phone": "18701029999" // 手机号
		}
	},{
		"groupNickname": "", //群成员昵称
		"role": 1,
		"createdAt": "2016-11-22T03:14:09.000Z",
		"createdTime": 1560222249000,
		"updatedAt": "2016-11-22T03:14:09.000Z",
		"updatedTime": 1560222249000,
		"user": {
			"id": "h6nEgcPC7",
			"nickname": "zl02",
			"portraitUri": ",
			"gender": "male", // 性别
			"stAccount": "b323422", // SealTalk 号
			"phone": "18701029999" // 手机号
		}
	}]
}
```

* displayName: 群名片
* role: 群角色
* createdAt: 创建时间
* updatedAt: 更改时间
* id: userId
* nickname: 用户名称
* portraitUri: 用户头像

返回码说明：

* 200: 请求成功
* 403: 用户不在群组中
* 404: 未知群组

### POST /group/set_certification

设置群认证

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|certiStatus|认证状态： 0 开启(需要认证)、1 关闭（不需要认证）|Number|是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求

### POST /group/agree

同意群邀请

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|receiverId|被邀请者 Id|String| 是|
|status|是否同意 0 忽略、 1 同意|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
}
```


### GET /group/notice_info

群通知邀请信息

#### 请求参数

```
无
```
#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": [
      {
        "id": "z2mlAvb0c",
        "status": 0, // 0: 忽略、1: 同意、2: 等待
        "type": 1 // 1: 待被邀请者处理、2: 待管理员处理
        "createdAt": "2019-07-18T05:37:12.000Z",
		"createdTime": "1563428232000",
        "requester": { // 邀请者信息
            "id": "z2mlAvb0c",
            "nickname": "群成员"
        },
        "receiver": { // 被邀请者信息
            "id": "uOavJZUpX",
            "nickname": "被邀请人"
        },
        "group": { // 群组信息
            "id": "kFpN4KiZn",
            "name": "测试群申请"
        }
      }
  ]
}

```

### POST /group/clear_notice

清空群验证通知消息

#### 请求参数

无

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```


### POST /group/mute_all

全员禁言

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|muteStatus|禁言状态：0 关闭 1 开启|Number|是|
|userId|可发言用户|Array|否|

userId 不传全员禁言，仅群组和管理员可发言

```
{
	"groupId":"1232s",
	"muteStatus": 1,
	"userId": "" //不设置传空	
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```



### POST /group/set_regular_clear

开启/更新 清理群离线消息

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|clearStatus|清理选项： 0 关闭、 3 清理 3 天前、 7 清理 7 天前、 36 清理 36 小时前 |Number|是|

```
{
	"groupId":"1232s",
	"clearStatus": 3 //清理 3 天前	
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```

### POST /group/get_regular_clear

获取群定时清理状态

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result" {
		"clearStatus":3 // 0 关闭、 3 清理 3 天前、 7 清理 7 天前、 36 清理 36 小时前
	}
}

```

### POST /group/set_member_info

设置群成员信息

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|memberId|群用户 Id |String|是|
|groupNickname| 群成员昵称 |String|否|
|region|区号|String|否|
|phone|电话 |String|否|
|WeChat|微信号 |String|否|
|Alipay|支付宝号 |String|否|
|memberDesc|描述|Array|否|

设置哪个传哪个，不传为不设置

```
{
	"groupNickname": "lee",
	"region": "86",
	"phone": "19990001233",
	"WeChat": "yt001",
	"Alipay":  "yt002",
	"memberDesc": "['描述1','描述2','描述3']"
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```

### POST /group/get_member_info

获取群成员信息

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|memberId|群用户 Id |String|是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result" {
		"groupNickname": 'lee',
		"phone": '19990001233',
		"WeChat":"yt001",
		"Alipay": "yt002",
		"memberDesc": "['描述1','描述2','描述3']"
	}
}

```

### POST /group/copy_group

复制群组

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群 Id|String| 是|
|name|群名称|String| 是|
|portraitUri|群头像 |String|是|

```
{
	"groupId": "dljpFS2",
	"name": "RongCloud",
	"portraitUri":"http://rongcloud-file.r"
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
	"result": {
		"id": "RfqHbcjes",// 群 Id
		"userStatus": [
			{
				"id": "uOavJZUpX",
				"status": 3 // 1 为已加入, 2 为等待管理员同意, 3 为等待被邀请者同意
			}
		]
	}
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求
* 20004: 群处于保护期
* 20005: 7 天内已被复制一次
* 20006: 群不存在或被解散


### POST /group/exited_list

退群列表

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群Id|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
	"result": [{
		"quitUserId": "djwe23", // 退群用户 Id
		"quitNickname": "珊珊", //退群用户昵称
		"quitPortraitUri": "http://rongcloud-file.r", //退群用户头像
		"quitReason": 0, // 0 被群主 张 移除群聊、 1 被管理员 张 移除、 2 主动退出
		"quitTime": '1562645863366' //退出时间
		"operatorId": "djw3sd", //操作者 Id
		"operatorName": '张', //管理员或群组名字
	},{
		"quitUserId": "djwe23", // 退群用户 Id
		"quitNickname": "珊珊", //退群用户昵称
		"quitPortraitUri": "http://rongcloud-file.r", //退群用户头像
		"quitReason": 0, // 0 被群主 张 移除群聊、 1 被管理员 张 移除、 2 主动退出
		"quitTime": '1562645863366' //退出时间
		"operatorId": "", //操作者 Id
		"operatorName": '', //管理员或群组名字
	}]
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求

### POST /group/set_member_protection

设置群成员保护模式

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|groupId|群Id|String| 是|
|memberProtection|成员保护模式: 0 关闭、1 开启|Number|是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	"code":200,
}
```

返回码说明：

* 200: 请求成功
* 400: 错误的请求
