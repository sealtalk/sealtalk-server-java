### 群组相关接口

| 接口地址 | 说明 |
|---------|-----| 
| [/misc/latest_update](#get-misclatest_update) | 注册新用户 |
| [/misc/client_version](#get-miscclient_version) | 用户登录 |
| [/misc/send_message](#post-miscsend_message) | 通过手机验证码设置新密码 |
| [/misc/set_screen_capture](#post-miscset_screen_capture) | 截屏通知设置 |
| [/misc/get_screen_capture](#post-miscget_screen_capture) | 获取截屏通知状态 |
| [/misc/send_sc_msg](#post-miscsend_sc_msg) | 发送截屏通知消息 |

## API 说明

### GET /misc/latest_update?version=1.0.0

获取客户端最新版本（ Desktop 使用 ）

#### 请求参数

```
version
```

* version: 三位版本号，例如 1.0.0

#### 返回结果

```
{
  "version": "1.0.2",
  "url": "http://downloads.rongcloud.cn/sealtalk/releases/mac/SealTalk_by_RongCloud_1.0.2.dmg",
  "name": "SealTalk 1.0.2",
  "notes": "bug fix",
  "pub_date": "2017-05-05"
}
```

* version: 最新版本
* url: 下载地址
* name: 包名
* notes: 更新日志
* pub_date: 最近一次更新日期

返回码说明：

* 204: 版本没有变化
* 400: 版本无效

### GET /misc/mobile_version

Android、iOS 获取更新版本, 返回 sealtalk 标准数据格式

#### 请求参数

```
无
```

#### 返回结果

```
{
  "code": 200,
  "result": {
    "iOS": {
      "version": "1.0.5",
      "build": "201607181821",
      "url": "https://dn-rongcloud.qbox.me/app.plist"
    },
    "Android": {
      "version": "1.0.5",
      "url": "http://downloads.rongcloud.cn/SealTalk_by_RongCloud_Android_v1_0_5.apk"
    }
  }
}

```

### GET /misc/client_version

Android、iOS 获取更新版本

#### 请求参数

```
无
```

#### 返回结果

```
{
  "iOS": {
    "version": "1.0.5",
    "build": "201607181821",
    "url": "https://dn-rongcloud.qbox.me/app.plist"
  },
  "Android": {
    "version": "1.0.5",
    "url": "http://downloads.rongcloud.cn/SealTalk_by_RongCloud_Android_v1_0_5.apk"
  }
}

```

### POST /misc/send_message

Server API 发送消息

#### 请求参数

```
{
	conversationType: 'PRIVATE',
	targetId: 'DS9ahdanm',
	objectName: 'RC:TxtMsg',
	content: '你好，Martin',
	pushContent: 'push 内容'
}
```

* conversationType: 会话类型 `PRIVATE` `GROUP`
* targetId: 接收者 Id
* objectName: 消息类型 `RC:TxtMsg` `RC:ImgMsg` 更多请查看 [消息类型](http://rongcloud.cn/docs/server.html#message_type)
* content: 消息内容
* pushContent: push 内容

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{
	code: "200"
}
```

返回码说明：

* 200: 发送成功
* 403: PRIVATE 与对方不是好友
* 403: GROUP 发送消息用户不在群组中
* 403: 未知会话类型，默认支持 PRIVATE、GROUP

### POST /misc/set_screen_capture

截屏通知设置

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|conversationType|会话类型：1 单聊、3 群聊|Number| 是|
|targetId| 目标 Id|String| 是|
|noticeStatus| 设置状态： 0 关闭 1 打开|Number| 是|

#### 消息体结构

```
开关截屏通知消息 ObjectName ：ST:ConNtf
{
    operatorUserId: encodedUserId,
    operation: 'openScreenNtf',  // openScreenNtf: 打开、  closeScreenNtf: 关闭
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
}

```

### POST /misc/get_screen_capture

截屏通知设置

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|conversationType|会话类型：1 单聊、3 群聊|Number| 是|
|targetId| 目标 Id|String| 是|

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200,
	"result": {
		"status": 0 // 0 关闭 1 打开		
	}
}

```

### POST /misc/send_sc_msg

发送截屏通知消息

|参数|说明|数据类型|是否必填|
|---|----|------|------|
|conversationType|会话类型：1 单聊、3 群聊|Number| 是|
|targetId| 目标 Id|String| 是|

#### 消息体结构

```
截屏通知消息 ObjectName ：ST:ConNtf
{
    operatorUserId: encodedUserId,
    operation: 'sendScreenNtf', 
}
```

#### 返回结果

正常返回，返回的 HTTP Status Code 为 200，返回的内容如下：

```
{	
	"code": 200
}

```