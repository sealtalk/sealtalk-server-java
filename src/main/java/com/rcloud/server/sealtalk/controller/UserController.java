package com.rcloud.server.sealtalk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.SmsManager;
import com.rcloud.server.sealtalk.manager.UserManager;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.AES256;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "用户相关")
@RestController
@RequestMapping("/user")
@Timed(percentiles = {0.9, 0.95, 0.99})
public class UserController {

    @Resource
    private UserManager userManager;

    @Resource
    private SmsManager smsManager;

    @Resource
    private SealtalkConfig sealtalkConfig;

    @ApiOperation(value = "向手机发送验证码(RongCloud)")
    @RequestMapping(value = "/send_code", method = RequestMethod.POST)
    public APIResult<String> sendCode(
            @ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
            @RequestParam String region,
            @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
            @RequestParam String phone
    ) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        userManager.sendCode(region, phone, SmsServiceType.RONGCLOUD, null);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "向手机发送验证码(云片服务)")
    @RequestMapping(value = "/send_code_yp", method = RequestMethod.POST)
    public APIResult<String> sendCodeYp(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                        @RequestParam String region,
                                        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                        @RequestParam String phone,
                                        HttpSession httpSession) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        Object object = httpSession.getAttribute(Constants.SERVER_API_PARAMS);
        Assert.notNull(object, "serverApiParams error");
        ServerApiParams serverApiParams = (ServerApiParams) object;
        userManager.sendCode(region, phone, SmsServiceType.YUNPIAN, serverApiParams);
        return APIResultWrap.ok("");
    }


    /**
     * 校验验证码(融云)
     * 1、根据region ,phone 查询验证码
     * -》如果没查询到，返回404，Unknown phone number
     * -》获取当前时间然后减去2分钟，和token的updateAt修改时间比较，判断是否在2分钟有效期内
     * 如果过期，返回2000，Verification code expired
     * -》如果是开发环境或者RONGCLOUD_SMS_REGISTER_TEMPLATE_ID为空，并且验证码参数 code==9999，直接返回成功200，并返回token {verification_token: verification.token}
     * <p>
     * 2、调用融云校验验证码接口
     * -》如果调用接口失败，返回融云接口的错误码和错误信息
     * -》如果调用接口成功，判断返回码
     * -》如果返回码不等于200，返回融云内部错误码和错误信息
     * -》如果返回码等于200，说明校验成功，返回token
     */
    @ApiOperation(value = "校验验证码")
    @RequestMapping(value = "/verify_code", method = RequestMethod.POST)
    public APIResult<String> verifyCode(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                        @RequestParam String region,
                                        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                        @RequestParam String phone,
                                        @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                        @RequestParam String code) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        String token = userManager.verifyCode(region, phone, code, SmsServiceType.RONGCLOUD);
        Map<String, String> result = new HashMap<>();
        result.put("verification_token", token);
        return APIResultWrap.ok(token);
    }


    /**
     * 校验验证码(云片)
     * 1、region处理，去掉前缀 + 号
     * 2、根据region ,phone 查询验证码
     * -》如果没查询到，返回404，Unknown phone number
     * -》获取当前时间然后减去2分钟，和token的updateAt修改时间比较，判断是否在2分钟有效期内
     * 如果过期，返回2000，Verification code expired
     * -》如果是开发环境，并且验证码参数 code==9999，直接返回成功200，并返回token {verification_token: verification.token}
     * 3、判断验证码是否正确  verification.sessionId == code
     * -》正确，返回200，token verification.token
     * -》错误，返回1000，Invalid verification code.
     */
    @ApiOperation(value = "校验验证码(云片服务)")
    @RequestMapping(value = "/verify_code_yp", method = RequestMethod.POST)
    public APIResult<String> verifyCodeYP(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                          @RequestParam String region,
                                          @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                          @RequestParam String phone,
                                          @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                          @RequestParam String code) throws ServiceException {
        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        region = MiscUtils.removeRegionPrefix(region);
        String token = userManager.verifyCode(region, phone, code, SmsServiceType.YUNPIAN);
        Map<String, String> result = new HashMap<>();
        result.put("verification_token", token);
        return APIResultWrap.ok(token);
    }


    @ApiOperation(value = "获取所有区域信息")
    @RequestMapping(value = "/regionlist", method = RequestMethod.GET)
    public APIResult<Object> regionlist() throws ServiceException {

        JsonNode jsonNode = smsManager.getRegionList();
        return APIResultWrap.ok(jsonNode);
    }


    @ApiOperation(value = "检查手机号是否可以注册")
    @RequestMapping(value = "/check_phone_available", method = RequestMethod.POST)
    public APIResult<Boolean> checkPhoneAvailable(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                                  @RequestParam String region,
                                                  @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                                  @RequestParam String phone) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        if (userManager.isExistUser(region, phone)) {
            return APIResultWrap.ok(false);
        } else {
            return APIResultWrap.ok(true);
        }
    }


    /**
     * 0、xss 转义处理 昵称字段
     * 1、密码不能有空格
     * 2、昵称长度[1,32]
     * 3、密码长度[6,20]
     * 4、verificationToken token是uuid格式
     * 5、verificationToken 是否在verification_codes表中存在
     * 6、检查该手机号(Region+phone)是否已经注册过，已经注册过，返回400
     * 7、如果没有注册过，hash生成密码，插入user表
     * 8、然后插入DataVersion表，然后设置cookie，缓存nickname，
     * 9、然后上报管理后台 TODO
     * 10、返回注册成功，200，用户主键Id编码
     */
    @ApiOperation(value = "注册新用户")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public APIResult<String> register(@ApiParam(name = "nickname", value = "昵称", required = true, type = "String", example = "xxx")
                                      @RequestParam String nickname,
                                      @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                      @RequestParam String password,
                                      @ApiParam(name = "verification_token", value = "校验Token", required = true, type = "String", example = "xxx")
                                      @RequestParam String verificationToken,
                                      HttpServletResponse response) throws ServiceException {

        checkRegisterParam(nickname, password, verificationToken);
        long id = userManager.register(nickname, password, verificationToken, response);
        return APIResultWrap.ok(N3d.encode(id));
    }

    private void checkRegisterParam(String nickname, String password, String verificationToken) throws ServiceException {
        ValidateUtils.checkPassword(password);
        ValidateUtils.checkNickName(nickname);
        ValidateUtils.checkUUID(verificationToken);
    }




    /**
     * 1、 判断phone、regionName合法性，不合法返回400
     * 2、 根据phone、region查询用户，查询不到返回1000，提示phone不存在
     * 3、 对明文密码加盐hash，验证密码是否正确，密码错误返回1001，提示错误的密码
     * 4、 埋cookie，缓存userid=nickname
     * 5、 查询该用户所属的所有组
     * 6、 将登录用户的userid，与groupIdName信息同步到融云
     * 7、 如果融云token为空，从融云获取token，如果融云token不为空，将userid、融云token返回给前段
     */
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResult<String> login(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                   @RequestParam String region,
                                   @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                   @RequestParam String phone,
                                   @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String password,
                                   HttpServletResponse response
    ) throws ServiceException {
        ValidateUtils.checkRegionName(MiscUtils.getRegionName(region));
        ValidateUtils.checkCompletePhone(phone);

        Pair<String, String> pairResult = userManager.login(region, phone, password, response);

        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("id", pairResult.getLeft());
        resultMap.put("token",pairResult.getRight());
        //对result编码
        return APIResultWrap.ok(MiscUtils.encodeResults(resultMap));
    }


    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public APIResult<String> logout(HttpServletResponse response) throws ServiceException {

        Cookie newCookie = new Cookie(sealtalkConfig.getAuthCookieName(), null);
        newCookie.setMaxAge(0);
        newCookie.setPath("/");
        response.addCookie(newCookie);
        return APIResultWrap.ok("");

    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public APIResult<String> resetPassword(@ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String password,
                                   @ApiParam(name = "verification_token", value = "凭证token", required = true, type = "String", example = "xxx")
                                   @RequestParam("verification_token") String verificationToken) throws ServiceException {


        ValidateUtils.checkPassword(password);
        ValidateUtils.checkUUID(verificationToken);

        userManager.resetPassword(password,verificationToken);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/change_password", method = RequestMethod.POST)
    public APIResult<String> changePassword(@ApiParam(name = "newPassword", value = "新密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String newPassword,
                                            @ApiParam(name = "oldPassword", value = "老密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String oldPassword,
                                            HttpServletRequest request) throws ServiceException {


        ValidateUtils.checkPassword(newPassword);
        ValidateUtils.notNull(oldPassword);

        Integer currentUserId = getCurrentUserId(request);
        userManager.changePassword(newPassword,oldPassword,currentUserId);
        return APIResultWrap.ok("");
    }


    /**
     * 设置当前用户昵称
     *
     * 1、xss处理nickname
     * 2、校验nickname参数合法性
     * 3、从cookie中获取当前用户id，根据id查询用户信息
     * 4、更新user表中的nickname,timestamp
     * 5、调用融云接口刷新昵称、缓存nickname到本地缓存
     * 6、更新DataVersion表的UserVersion、AllFriendshipVersion
     * 7、移除缓存信息
     *      -》Cache.del("user_" + currentUserId);
     *      -》Cache.del("friendship_profile_user_" + currentUserId);
     * 8、根据currentUserId 查询所有的friendId，然后清除缓存Cache.del("friendship_all_" + friend.friendId)
     * 9、根据currentUserId查询所有的groupId，然后清除缓存Cache.del("group_members_" + groupMember.groupId)
     * 10、成功后返回200
     */

    @ApiOperation(value = "设置昵称")
    @RequestMapping(value = "/set_nickname", method = RequestMethod.POST)
    public APIResult<String> setNickName(@ApiParam(name = "nickname", value = "昵称", required = true, type = "String", example = "xxx")
                                         @RequestParam String nickname,
                                            HttpServletRequest request) throws ServiceException {


        ValidateUtils.checkNickName(nickname);

        Integer currentUserId = getCurrentUserId(request);
        userManager.setNickName(nickname,currentUserId);
        return APIResultWrap.ok("");
    }




    private Integer getCurrentUserId(HttpServletRequest request) {
        Integer userId =null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(sealtalkConfig.getAuthCookieName())){
                    userId = Integer.valueOf(AES256.decrypt(cookie.getValue().getBytes(),sealtalkConfig.getAuthCookieKey()));
                }
            }
        }
        return null;
    }
}
