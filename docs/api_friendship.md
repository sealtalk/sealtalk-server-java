### 好友相关接口

| 接口地址 | 说明 |
|---------|-----|
| [/friendship/invite](#post-friendshipinvite) | 发起添加好友 |
| [/friendship/agree](#post-friendshipagree) | 同意加好友请求 |
| [/friendship/ignore](#post-friendshipignore) | 忽略好友请求 |
| [/friendship/delete](#post-friendshipdelete) | 删除好友请求 |
| [/friendship/set_display_name](#post-friendshipset_display_name) | 设置好友备注名 |
| [/friendship/all](#get-friendshipall) | 获取好友列表 |
| [/friendship/:friendId/profile](#get-friendshipfriendidprofile) | 获取好友信息 |
| [/friendship/get_contacts_info](#post-friendshipgetcontactsinfo) | 获取通讯录朋友信息列表 |
|[/friendship/batch_delete](#post-friendshipbatchdelete)|批量删除好友|
|[/friendship/set_friend_description](#post-friendshipsetfrienddescription)|设置朋友备注和描述|
|[/friendship/get_friend_description](#post-friendshipgetfrienddescription)|获取朋友备注和描述|
## API 说明

### POST /friendship/invite

添加好友

#### 请求参数

```
{
     "friendId": "RfqHbcjes",
     "message": "你好，我是 Martin"
}
```

* friendId: 好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": {
		"action": "Added"
	}
}
```

* action: 添加好友请求状态 `Added: 已添加` `None: 在对方黑名单中` `Sent: 请求已发送` `AddDirectly: 直接添加对方`

返回码说明：

* 200: 请求成功
* 400: 已经是好友

### POST /friendship/agree

同意好友请求

#### 请求参数

```
{
     "friendId": "RfqHbcjes"
}
```

* friendId: 好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": {
		"action": "Added"
	}
}
```

* action: 添加好友请求状态 `Added: 已添加` `None: 在对方黑名单中` `Sent: 请求已发送` 

返回码说明：

* 200: 请求成功
* 404: 无效的好友请求或未知好友

### POST /friendship/ignore

忽略好友请求

#### 请求参数

```
{
     "friendId": "RfqHbcjes"
}
```

* friendId:  好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 404: 无效的好友请求或未知好友

### POST /friendship/delete

删除好友

#### 请求参数

```
{
     "friendId": "RfqHbcjes"
}
```

* friendId: 好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 404: 无效的好友请求或未知好友

### POST /friendship/set_display_name

设置好友备注名称

#### 请求参数

```
{
     "friendId": "RfqHbcjes",
     "displayName": "备注"
}
```

* friendId: 好友 Id

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200
}
```

返回码说明：

* 200: 请求成功
* 404: 无效的好友请求或未知好友
* 400: 备注名称超限

### GET /friendship/all

获取好友列表

#### 请求参数

```
无
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": [{
		"displayName": "Martin",
		"message": "你好，我是一杯水",
		"status": 10,
		"updatedAt": "2017-09-18",
		"updatedTime": "1560222477000",
		"user": {
			"id": "slEcpCI63",
			"nickname": "一杯水",
			"region": "86",
			"phone": "13269772766",
			"portraitUri": "http://7xogjk.com1.z0.glb.clouddn.com/Fo6wxS7zzvGpwyAFhlpTUVirpOGh",
			"gender": "male", // 性别
			"stAccount": "b323422", // SealTalk 号
			"phone": "18701029999" // 手机号
		}
	}]
}

```

* displayName: 好友备注
* message: 请求加好友描述
* status: 好友关系状态 10: 请求, 11: 被请求, 20: 同意, 21: 忽略, 30: 被删除
* updatedAt: 最后一次好友状态修改时间
* user: 加好友请求发起方用户信息
* user.id: Id
* user.nickname: 昵称
* user.region: 手机号区域标识
* user.phone: 手机号
* user.portraiUri: 头像

返回码说明：

* 200: 请求成功

### GET /friendship/:friendId/profile

获取好友信息

#### 请求参数

```
friendId: 好友 Id
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	"code":200,
	"result": {
		"displayName": "Wee",
		"user": {
			"id":"g891BoDvN",
			"nickname":"Tina",
			"region":"86",
			"phone":"18221252163",
			"portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FjsNMjYoVKfGmA86SNwnggfKgE6_"
		}
	}
}
```

* displayName: 好友备注
* user: 好友信息
* user.id: Id
* user.nickname: 昵称
* user.region: 手机号区域标识
* user.phone: 手机号
* user.portraiUri: 头像

返回码说明：

* 200: 请求成功
* 403: friendId 非当前用户好友

### POST /friendship/get_contacts_info

获取通讯录朋友信息列表 （手机端传入手机列表，server 返回列表信息）

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|contacstList|手机号列表|Array| 是|

```
{
	contactList: ['13099990000','13912349090']
}
```
#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": [{
		"registered": 1, // 0 未注册 1 已注册
		"relationship": 0, // 0 非好友 1 好友
		"stAccount": "ST64532", // sealtalk 号
		"phone": "18700002234",
		"id", "se2fd23", // 次用户 id
		"nickname": "Tom", // 昵称
		"portraitUri": "http://test.com/user/abc123.jpg" // 头像
	},{
		"registered": 0,
		"relationship": "", // 0 非好友 1 好友
		"stAccount": "",
		"phone": "18700002234",
		"id", "",
		"nickname": "",
		"portraitUri": ""
	},{
		"registered": 1,
		"relationship": 1, // 0 非好友 1 好友
		"stAccount": "",
		"phone": "18700002234",
		"id", "se2fd23",
		"nickname": "Tom",
		"portraitUri": "http://test.com/user/abc123.jpg"
	}]
}

```

### POST /friendship/batch_delete

批量删除好友

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|friendIds|好友 Id|Array| 是|

```
{
    "friendIds": ["RfqHbcjes","RfqHbcjes"]
}
```


#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200
}
```

返回码说明：

* 200: 请求成功

### POST /friendship/set_friend_description

设置朋友备注和描述

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|friendId|朋友 id |String| 是|
|displayName|备注 |String | 否|
|region|国家区号|String|否|
|phone|手机号 |String| 否|
|description|更多描述 |String | 否|
|imageUri|照片地址 |String| 否|

设置哪项传哪项，不传为不设置,设置为空,传空字符串

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```
## POST /friendship/get_friend_description

获取朋友备注和描述

#### 请求参数

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|friendId|朋友 id |String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"displayName": 'Fox', 
	"region": '86', 
	"phone": '18700991234', 
	"description": '融云该公司地址在北京市朝阳区北苑路北。', 
	"imageUri": 'http://rongcloud-file.ronghub.com/cb6d05474f891251ae.PNG', 
}

```


