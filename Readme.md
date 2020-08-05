#JDK版本
由于jdk 8u161 之前加密无限强度关闭 长度256会有问题 ，要求jdk版本 8u161 以上

#项目配置



## 注意事项(必读)

1、配置七牛空间, 必须为`华东`存储空间. 如需其他空间, 需修改 SealTalk Server 源码 `src/routes/user.js` get_image_token 接口获取七牛 token 参数

2、客户端默认调用云片短信服务(send_code_yp、verify_code_yp), 如需修改短信服务, 可参考 `src/util/sms.js`

3、开发环境下(NODE_ENV 为 development), 不需要收真实短信, 点击发送验证码后, 输入 `9999` 即可注册

4、`AUTH_COOKIE_DOMAIN` 和 `CORS_HOSTS` 配置项必须按照上述说明配置正确

## 业务数据配置 (无需求略过)

client_version.json : 配置 SealTalk 移动端的最新 App 版本号、下载地址等信息。

squirrel.json : 配置 SealTalk Desktop 端的最新 App 版本号、下载地址等信息。

demo_square.json : 配置 SealTalk 移动端“发现”频道中的默认聊天室和群组数据。
