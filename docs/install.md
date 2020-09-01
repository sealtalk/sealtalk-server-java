# 配置安装与开发调试

## 安装 JAVA运行环境 

**要求 JDK 版本 8u161 及以上**

[JAVA 官网](https://www.oracle.com/java/technologies/javase-downloads.html)

## 安装 MySQL

[MySQL 官网](https://www.mysql.com/)

## 项目配置

请修改 [application.yml](../src/main/resources/application.yml) 文件中的相关配置配置, 详细请参看 [application.yml](../src/main/resources/application.yml) 中的注释和示例

```yaml
sealtalk-config:
  # 认证 Cookie 名称，请根据业务自行定义，如：rong_im_auth
  auth_cookie_name: rong_auth_cookie  
  # 认证 Cookie 加密密钥，请自行定义，任意字母数字组合                     
  auth_cookie_key: zsyy
  # 认证 Cookie 过期时间，有效期单位秒 8640000=100天
  auth_cookie_max_age: 8640000      
  # 融云颁发的 App Key，请访问融云开发者后台：https://developer.rongcloud.cn
  rongcloud_app_key: bmdehs6pbaauls             
  # 融云颁发的 App Secret，请访问融云开发者后台：https://developer.rongcloud.cn
  rongcloud_app_secret: xvQhSJbagYBtwF
  # 融云颁发的 Api Url, 逗号分割，第一个为主域名，后面的为备份域名
  rongcloud_api_url: api-sg01.ronghub.com,api-xxx.ronghub.com       
  # 融云短信服务提供的注册用户短信模板 Id
  rongcloud_sms_register_template_id: 3910922   # 短信模版ID
  # 七牛颁发的 Access Key，请访问七牛开发者后台：https://portal.qiniu.com
  qiniu_access_key: ctk1VIyQlx5CaFr_EQMqrQkF5c1PKGuqC2SNTR749  
  # 七牛颁发的 Secret Key，请访问七牛开发者后台：https://portal.qiniu.com
  qiniu_secret_key: MRuJJ6AcBkFQargm16ayDtv_4L0M2jsQe-QuMBfiw  
  # 七牛创建的空间名称，请访问七牛开发者后台：https://portal.qiniu.com
  qiniu_bucket_name: devtalk-image
  # 七牛创建的空间域名，请访问七牛开发者后台：https://portal.qiniu.com
  qiniu_bucket_domain: self.domain.com
  # 云片颁发的 APi Key, 请访问云片开发者后台: https://www.yunpian.com/admin/main
  yunpian_api_key: 830c36ecb0eaf1667e02769e1f33d9942           
  # 云片短信发送接口频率限制(根据IP地址)，自定义配置,不配置默认不限制
  yunpian_limited_time: 1     #单位小时 ，1小时，自定义配置
  yunpian_limited_count: 100   #云片发送验证码次数限制 20次
  # N3D 密钥，用来加密所有的 Id 数字，不小于 5 位的字母数字组合
  n3d_key: 11EdDIaqpcim
  # 认证 Cookie 主域名 如果没有正式域名，请修改本地 hosts 文件配置域名，此处设置 Cookie 主域名， 必须和 CORS_HOSTS 配置项在相同的顶级域下
  auth_cookie_domain: devtalk.im      
  # 跨域支持所需配置的域名信息，包括请求服务器的域名和端口号，如果是 80 端口可以省略端口号。如：http://web.sealtalk.im
  cors_hosts: http://web.devtalk.im 
  exclude_url: /misc/demo_square,/misc/latest_update, /user/verify_code_yp_t,/misc/client_version,/misc/mobile_version,/user/login,/user/register,/user/reset_password, /user/send_code, /user/send_code_yp,/user/verify_code, /user/verify_code_yp, /user/delete, /user/get_sms_img_code,/user/check_username_available,/user/check_phone_available,/user/regionlist,/ping
  # 本服务监听的 HTTP 端口号
  server_port: 8080   
  # MySQL 数据库名称      
  db_name: sealtalk   
  # MySQL 数据库用户名       
  db_user: root
  # MySQL 数据库密码
  db_password: 123456
  # MySQL 数据库服务器地址
  db_host: 127.0.0.1
  # MySQL 数据库服务端口号
  db_port: 3306

```

### 修改环境变量

请修改 [application.yml](../src/main/resources/application.yml) 文件中的相关配置配置, 详细请参看 [application.yml](../src/main/resources/application.yml) 中的注释和示例
```yaml
spring:
  profiles:
    active: dev      # 环境配置 dev 开发环境，pro 生产环境
```

### 数据库初始化

数据库版本管理工具 使用了 [flyway](https://flywaydb.org/) 插件,相关配置在[application.yml](../src/main/resources/application.yml) 文件中：

```yaml
spring:
  flyway:
    enabled: true
    # 禁止清理数据库表
    clean-disabled: true
    baseline-description: baselines
    # 如果数据库不是空表，需要设置成 true，否则启动报错
    baseline-on-migrate: true
    baseline-version: 2
```
 
需建立一个空的数据库，项目启动时会自动初始化表结构.

### 启动服务

1、独立tomcat部署： 项目编译打包生产war包

2、内嵌tomcat方式： 项目编译打包生产Jar包，java -jar 命令启动

## 注意事项(必读)

1、开发环境下, 不需要收真实短信, 点击发送验证码后, 输入 `9999` 即可注册

2、`AUTH_COOKIE_DOMAIN` 和 `CORS_HOSTS` 配置项必须按照上述说明配置正确

## 业务数据配置 (无需求略过)

client_version.json : 配置 SealTalk 移动端的最新 App 版本号、下载地址等信息。

squirrel.json : 配置 SealTalk Desktop 端的最新 App 版本号、下载地址等信息。

demo_square.json : 配置 SealTalk 移动端“发现”频道中的默认聊天室和群组数据。
