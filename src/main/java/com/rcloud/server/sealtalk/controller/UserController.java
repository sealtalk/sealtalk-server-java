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
import com.rcloud.server.sealtalk.model.response.Response;
import com.rcloud.server.sealtalk.model.response.ResultWrap;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @Author: xiuwei.nie, Jianlu.Yu
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
    public Response<String> sendCode(
            @ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
            @RequestParam String region,
            @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
            @RequestParam String phone
    ) throws ServiceException {

        userManager.sendCode(region, phone, SmsServiceType.RONGCLOUD, null);
        return ResultWrap.ok("");
    }

    @ApiOperation(value = "向手机发送验证码(云片服务)")
    @RequestMapping(value = "/send_code_yp", method = RequestMethod.POST)
    public Response<String> sendCodeYp(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                       @RequestParam String region,
                                       @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                       @RequestParam String phone,
                                       HttpSession httpSession) throws ServiceException {

        Object object = httpSession.getAttribute(Constants.SERVER_API_PARAMS);
        Assert.notNull(object, "serverApiParams error");
        ServerApiParams serverApiParams = (ServerApiParams) object;
        userManager.sendCode(region, phone, SmsServiceType.YUNPIAN, serverApiParams);
        return ResultWrap.ok("");
    }


    /**
     * 校验验证码(融云)
     * 1、根据region ,phone 查询验证码
     *      -》如果没查询到，返回404，Unknown phone number
     *      -》获取当前时间然后减去2分钟，和token的updateAt修改时间比较，判断是否在2分钟有效期内
     *         如果过期，返回2000，Verification code expired
     *      -》如果是开发环境或者RONGCLOUD_SMS_REGISTER_TEMPLATE_ID为空，并且验证码参数 code==9999，直接返回成功200，并返回token {verification_token: verification.token}
     *
     * 2、调用融云校验验证码接口
     *      -》如果调用接口失败，返回融云接口的错误码和错误信息
     *      -》如果调用接口成功，判断返回码
     *          -》如果返回码不等于200，返回融云内部错误码和错误信息
     *          -》如果返回码等于200，说明校验成功，返回token
     */
    @ApiOperation(value = "校验验证码")
    @RequestMapping(value = "/verify_code", method = RequestMethod.POST)
    public Response<String> verifyCode(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                       @RequestParam String region,
                                       @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                       @RequestParam String phone,
                                       @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                       @RequestParam String code) throws ServiceException {


        String token = userManager.verifyCode(region, phone, code, SmsServiceType.RONGCLOUD);
        Map<String, String> result = new HashMap<>();
        result.put("verification_token", token);
        return ResultWrap.ok(token);
    }


    /**
     * 校验验证码(云片)
     * 1、region处理，去掉前缀 + 号
     * 2、根据region ,phone 查询验证码
     *      -》如果没查询到，返回404，Unknown phone number
     *      -》获取当前时间然后减去2分钟，和token的updateAt修改时间比较，判断是否在2分钟有效期内
     *          如果过期，返回2000，Verification code expired
     *      -》如果是开发环境，并且验证码参数 code==9999，直接返回成功200，并返回token {verification_token: verification.token}
     * 3、判断验证码是否正确  verification.sessionId == code
     *      -》正确，返回200，token verification.token
     *      -》错误，返回1000，Invalid verification code.
     */
    @ApiOperation(value = "校验验证码(云片服务)")
    @RequestMapping(value = "/verify_code_yp", method = RequestMethod.POST)
    public Response<String> verifyCodeYP(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                         @RequestParam String region,
                                         @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                         @RequestParam String phone,
                                         @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                         @RequestParam String code) throws ServiceException {
        region = MiscUtils.removeRegionPrefix(region);
        String token = userManager.verifyCode(region, phone, code, SmsServiceType.YUNPIAN);
        Map<String, String> result = new HashMap<>();
        result.put("verification_token", token);
        return ResultWrap.ok(token);
    }


    @ApiOperation(value = "获取所有区域信息")
    @RequestMapping(value = "/regionlist", method = RequestMethod.GET)
    public Response<Object> regionlist() throws ServiceException {

        JsonNode jsonNode = smsManager.getRegionList();
        return ResultWrap.ok(jsonNode);
    }


    @ApiOperation(value = "检查手机号是否可以注册")
    @RequestMapping(value = "/check_phone_available", method = RequestMethod.POST)
    public Response<Boolean> checkPhoneAvailable(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                                 @RequestParam String region,
                                                 @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                                 @RequestParam String phone) throws ServiceException {

        if (userManager.isExistUser(region, phone)) {
            return ResultWrap.ok(false);
        } else {
            return ResultWrap.ok(true);
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
    public Response<String> register(@ApiParam(name = "nickname", value = "昵称", required = true, type = "String", example = "xxx")
                                      @RequestParam String nickname,
                                      @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                      @RequestParam String password,
                                      @ApiParam(name = "verification_token", value = "校验Token", required = true, type = "String", example = "xxx")
                                      @RequestParam String verificationToken) throws ServiceException {

        checkRegisterParam(nickname,password,verificationToken);
        long id = userManager.register(nickname,password,verificationToken);
        return ResultWrap.ok(N3d.encode(id));
    }

    private void checkRegisterParam(String nickname,String password,String verificationToken) throws ServiceException{
        if(password.indexOf(" ")>-1){
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }
        if(StringUtils.isEmpty(nickname) || nickname.length()>32){
            throw new ServiceException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if(StringUtils.isEmpty(password) || password.length()<6 || password.length()>20){
            throw new ServiceException(ErrorCode.INVALID_PASSWORD_LENGHT);
        }
        if(!ValidateUtils.checkUUIDStr(verificationToken)){
            throw new ServiceException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

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
    public Response<Boolean> login(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                   @RequestParam String region,
                                   @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "18811111111")
                                   @RequestParam String phone,
                                   @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String password
    ) throws ServiceException {




        return ResultWrap.ok(false);
    }


    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Response<String> logout(HttpServletResponse response) throws ServiceException {

        Cookie newCookie=new Cookie(sealtalkConfig.getAuthCookieName(),null);
        newCookie.setMaxAge(0);
        newCookie.setPath("/");
        response.addCookie(newCookie);
        return ResultWrap.ok("");

    }
}
