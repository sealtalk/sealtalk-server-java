package com.rcloud.server.sealtalk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.domain.BlackLists;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.UserManager;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.model.dto.FavGroupsDTO;
import com.rcloud.server.sealtalk.model.dto.SyncInfoDTO;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.*;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
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
public class UserController extends BaseController {

    @Resource
    private UserManager userManager;


    @ApiOperation(value = "向手机发送验证码(RongCloud)")
    @RequestMapping(value = "/send_code", method = RequestMethod.POST)
    public APIResult<Object> sendCode(
            @ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
            @RequestParam String region,
            @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
            @RequestParam String phone
    ) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        userManager.sendCode(region, phone, SmsServiceType.RONGCLOUD, null);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "向手机发送验证码(云片服务)")
    @RequestMapping(value = "/send_code_yp", method = RequestMethod.POST)
    public APIResult<Object> sendCodeYp(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                        @RequestParam String region,
                                        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
                                        @RequestParam String phone) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        ServerApiParams serverApiParams = getServerApiParams();
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
    public APIResult<Object> verifyCode(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                        @RequestParam String region,
                                        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
                                        @RequestParam String phone,
                                        @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                        @RequestParam String code) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        String token = userManager.verifyCode(region, phone, code, SmsServiceType.RONGCLOUD);
        Map<String, String> result = new HashMap<>();
        result.put(Constants.VERIFICATION_TOKEN_KEY, token);
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
    public APIResult<Object> verifyCodeYP(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                          @RequestParam String region,
                                          @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
                                          @RequestParam String phone,
                                          @ApiParam(name = "code", value = "验证码", required = true, type = "String", example = "xxxxxx")
                                          @RequestParam String code) throws ServiceException {
        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        region = MiscUtils.removeRegionPrefix(region);
        String token = userManager.verifyCode(region, phone, code, SmsServiceType.YUNPIAN);
        Map<String, String> result = new HashMap<>();
        result.put(Constants.VERIFICATION_TOKEN_KEY, token);
        return APIResultWrap.ok(result);
    }


    @ApiOperation(value = "获取所有区域信息")
    @RequestMapping(value = "/regionlist", method = RequestMethod.GET)
    public APIResult<Object> regionlist() throws ServiceException {
        JsonNode jsonNode = userManager.getRegionList();
        return APIResultWrap.ok(jsonNode);
    }


    @ApiOperation(value = "检查手机号是否可以注册")
    @RequestMapping(value = "/check_phone_available", method = RequestMethod.POST)
    public APIResult<Boolean> checkPhoneAvailable(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                                  @RequestParam String region,
                                                  @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
                                                  @RequestParam String phone) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        if (userManager.isExistUser(region, phone)) {
            return APIResultWrap.ok(false, "Phone number has already existed.");
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
     * 9、然后上报管理后台
     * 10、返回注册成功，200，用户主键Id编码
     */
    @ApiOperation(value = "注册新用户")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public APIResult<Object> register(@ApiParam(name = "nickname", value = "昵称", required = true, type = "String", example = "xxx")
                                      @RequestParam String nickname,
                                      @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                      @RequestParam String password,
                                      @ApiParam(name = "verification_token", value = "校验Token", required = true, type = "String", example = "xxx")
                                      @RequestParam String verification_token,
                                      HttpServletResponse response) throws ServiceException {

        nickname = MiscUtils.xss(nickname, ValidateUtils.NICKNAME_MAX_LENGTH);
        checkRegisterParam(nickname, password, verification_token);
        Integer id = userManager.register(nickname, password, verification_token);
        //设置cookie
        setCookie(response, id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", N3d.encode(id));

        return APIResultWrap.ok(resultMap);
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
     * 4、 埋cookie，缓存userid-》nickname
     * 5、 查询该用户所属的所有组
     * 6、 将登录用户的userid、groupIdName信息同步到融云
     * 7、 如果融云token为空，从融云获取token，如果融云token不为空，将userid、融云token返回给前端
     */
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResult<Object> login(@ApiParam(name = "region", value = "区号", required = true, type = "String", example = "86")
                                   @RequestParam String region,
                                   @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "188xxxxxxxx")
                                   @RequestParam String phone,
                                   @ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                   @RequestParam String password,
                                   HttpServletResponse response
    ) throws ServiceException {
        ValidateUtils.checkRegionName(MiscUtils.getRegionName(region));
        ValidateUtils.checkCompletePhone(phone);

        Pair<Integer, String> pairResult = userManager.login(region, phone, password);

        //设置cookie  userId加密存入cookie
        //登录成功后的其他请求，当前登录用户useId获取从cookie中获取
        setCookie(response, pairResult.getLeft());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", pairResult.getLeft());
        resultMap.put("token", pairResult.getRight());
        //对result编码
        return APIResultWrap.ok(MiscUtils.encodeResults(resultMap));
    }


    @ApiOperation(value = "用户注销")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public APIResult<Object> logout(HttpServletResponse response) throws ServiceException {

        Cookie newCookie = new Cookie(getSealtalkConfig().getAuthCookieName(), null);
        newCookie.setMaxAge(0);
        newCookie.setPath("/");
        response.addCookie(newCookie);
        return APIResultWrap.ok("");

    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public APIResult<Object> resetPassword(@ApiParam(name = "password", value = "密码", required = true, type = "String", example = "xxx")
                                           @RequestParam String password,
                                           @ApiParam(name = "verification_token", value = "凭证token", required = true, type = "String", example = "xxx")
                                           @RequestParam("verification_token") String verificationToken) throws ServiceException {


        ValidateUtils.checkPassword(password);
        ValidateUtils.checkUUID(verificationToken);

        userManager.resetPassword(password, verificationToken);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/change_password", method = RequestMethod.POST)
    public APIResult<Object> changePassword(@ApiParam(name = "newPassword", value = "新密码", required = true, type = "String", example = "xxx")
                                            @RequestParam String newPassword,
                                            @ApiParam(name = "oldPassword", value = "老密码", required = true, type = "String", example = "xxx")
                                            @RequestParam String oldPassword,
                                            HttpServletRequest request) throws ServiceException {


        ValidateUtils.checkPassword(newPassword);
        ValidateUtils.notEmpty(oldPassword);

        Integer currentUserId = getCurrentUserId(request);
        userManager.changePassword(newPassword, oldPassword, currentUserId);
        return APIResultWrap.ok("");
    }


    /**
     * 设置当前用户昵称
     * <p>
     * 1、xss处理nickname
     * 2、校验nickname参数合法性
     * 3、从cookie中获取当前用户id，根据id查询用户信息
     * 4、更新user表中的nickname,timestamp
     * 5、调用融云接口刷新昵称、缓存nickname到本地缓存
     * 6、更新DataVersion表的UserVersion、AllFriendshipVersion
     * 7、移除缓存信息
     * -》Cache.del("user_" + currentUserId);
     * -》Cache.del("friendship_profile_user_" + currentUserId);
     * 8、根据currentUserId 查询所有的friendId，然后清除缓存Cache.del("friendship_all_" + friend.friendId)
     * 9、根据currentUserId查询所有的groupId，然后清除缓存Cache.del("group_members_" + groupMember.groupId)
     * 10、成功后返回200
     */

    @ApiOperation(value = "设置昵称")
    @RequestMapping(value = "/set_nickname", method = RequestMethod.POST)
    public APIResult<Object> setNickName(@ApiParam(name = "nickname", value = "昵称", required = true, type = "String", example = "xxx")
                                         @RequestParam String nickname,
                                         HttpServletRequest request) throws ServiceException {
        nickname = MiscUtils.xss(nickname, ValidateUtils.NICKNAME_MAX_LENGTH);
        ValidateUtils.checkNickName(nickname);

        Integer currentUserId = getCurrentUserId(request);
        userManager.setNickName(nickname, currentUserId);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "设置头像")
    @RequestMapping(value = "/set_portrait_uri", method = RequestMethod.POST)
    public APIResult<Object> setPortraitUri(@ApiParam(name = "portraitUri", value = "头像", required = true, type = "String", example = "xxx")
                                            @RequestParam String portraitUri,
                                            HttpServletRequest request) throws ServiceException {

        portraitUri = MiscUtils.xss(portraitUri, ValidateUtils.PORTRAIT_URI_MAX_LENGTH);

        ValidateUtils.checkURLFormat(portraitUri);
        ValidateUtils.checkPortraitUri(portraitUri);

        Integer currentUserId = getCurrentUserId(request);
        userManager.setPortraitUri(portraitUri, currentUserId);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取融云token")
    @RequestMapping(value = "/get_token", method = RequestMethod.POST)
    public APIResult<Object> getToken(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        Pair<Integer, String> pairResult = userManager.getToken(currentUserId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", pairResult.getLeft());
        resultMap.put("token", pairResult.getRight());

        return APIResultWrap.ok(MiscUtils.encodeResults(resultMap));
    }


    @ApiOperation(value = "获取当前用户黑名单列表")
    @RequestMapping(value = "/blacklist", method = RequestMethod.GET)
    public APIResult<Object> blacklist(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        List<BlackLists> resultList = userManager.getBlackList(currentUserId);

        //TODO
        Object object = MiscUtils.encodeResults(resultList, "userId", "friendId", "users.id");
        String result = MiscUtils.addUpdateTimeToList(JacksonUtil.toJson(object));

        return APIResultWrap.ok(object);
    }


    @ApiOperation(value = "将好友加入黑名单")
    @RequestMapping(value = "/add_to_blacklist", method = RequestMethod.POST)
    public APIResult<Object> addBlackList(
            @ApiParam(name = "friendId", value = "好友ID", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            @ApiParam(name = "encodedFriendId", value = "encodedFriendId", required = true, type = "String", example = "xxx")
            @RequestParam String encodedFriendId,
            HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        userManager.addBlackList(currentUserId, Integer.valueOf(friendId), encodedFriendId);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "将好友移除黑名单")
    @RequestMapping(value = "/remove_from_blacklist", method = RequestMethod.POST)
    public APIResult<Object> removeBlacklist(
            @ApiParam(name = "friendId", value = "好友ID", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            @ApiParam(name = "encodedFriendId", value = "encodedFriendId", required = true, type = "String", example = "xxx")
            @RequestParam String encodedFriendId,
            HttpServletRequest request) throws ServiceException {


        Integer currentUserId = getCurrentUserId(request);
        userManager.removeBlackList(currentUserId, Integer.valueOf(friendId), encodedFriendId);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取七牛云存储token")
    @RequestMapping(value = "/get_image_token", method = RequestMethod.POST)
    public APIResult<Object> getImageToken() throws ServiceException {

        String token = userManager.getImageToken();

        Map<String, Object> map = new HashMap<>();
        map.put("target", "qiniu");
        map.put("domain", sealtalkConfig.getQiniuBucketDomain());
        map.put("token", token);
        return APIResultWrap.ok(map);
    }


    @ApiOperation(value = "获取短信图片验证码")
    @RequestMapping(value = "/get_sms_img_code", method = RequestMethod.POST)
    public APIResult<Object> getSmsImgCode() throws ServiceException {

        String result = userManager.getSmsImgCode();

        JsonNode jsonNode = JacksonUtil.getJsonNode(result);

        if (jsonNode.get("code").toString().equals("200")) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("url", jsonNode.get("url"));
            resultMap.put("verifyId", jsonNode.get("verifyId"));
            return APIResultWrap.ok(resultMap);
        } else {
            throw new ServiceException(ErrorCode.SERVER_ERROR, "RongCloud Server API Error Code: " + jsonNode.get("code"));
        }
    }

    @ApiOperation(value = "获取当前用户所属群组")
    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public APIResult<Object> getGroups(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        List<Groups> groupsList = userManager.getGroups(currentUserId);

        return APIResultWrap.ok(MiscUtils.encodeResults(groupsList, "id", "creatorId"));
    }

    @ApiOperation(value = "同步用户的好友、黑名单、群组、群组成员数据")
    @RequestMapping(value = "/sync/{version}", method = RequestMethod.POST)
    public APIResult<Object> syncInfo(@ApiParam(name = "version", value = "请求的版本号(时间戳)", required = true, type = "String", example = "xxx")
                                      @PathVariable("version") String version,
                                      HttpServletRequest request) throws ServiceException {

        ValidateUtils.checkTimeStamp(version);

        Integer currentUserId = getCurrentUserId(request);

        SyncInfoDTO syncInfoDTO = userManager.getSyncInfo(currentUserId, Long.valueOf(version));
        return APIResultWrap.ok(syncInfoDTO);
    }


    @ApiOperation(value = "根据手机号查找用户信息")
    @RequestMapping(value = "/find/{region}/{phone}", method = RequestMethod.POST)
    public APIResult<Object> getUserByPhone(@ApiParam(name = "region", value = "region", required = true, type = "String", example = "xxx")
                                            @PathVariable("region") String region,
                                            @ApiParam(name = "phone", value = "phone", required = true, type = "String", example = "xxx")
                                            @PathVariable("phone") String phone,
                                            HttpServletRequest request) throws ServiceException {
        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);

        Users users = userManager.getUser(region, phone);
        if (users != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", users.getId());
            map.put("nickname", users.getNickname());
            map.put("portraitUri", users.getPortraitUri());
            return APIResultWrap.ok(MiscUtils.encodeResults(map));
        } else {
            throw new ServiceException(ErrorCode.UNKNOW_USER);
        }
    }

    @ApiOperation(value = "获取用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public APIResult<Object> getUserInfo(@ApiParam(name = "id", value = "用户ID", required = true, type = "Integer", example = "xxx")
                                         @PathVariable("id") String id,
                                         HttpServletRequest request) throws ServiceException {

        Integer userId = N3d.decode(id);
        Users users = userManager.getUser(userId);
        if (users != null) {
            Users t_user = new Users();
            t_user.setId(users.getId());
            t_user.setNickname(users.getNickname());
            t_user.setPortraitUri(users.getPortraitUri());
            t_user.setGender(users.getGender());
            t_user.setStAccount(users.getStAccount());
            t_user.setPhone(users.getPhone());
            return APIResultWrap.ok(MiscUtils.encodeResults(t_user));
        } else {
            throw new ServiceException(ErrorCode.UNKNOW_USER);
        }
    }


    @ApiOperation(value = "获取通讯录群组")
    @RequestMapping(value = "/favgroups", method = RequestMethod.GET)
    public APIResult<Object> getFavGroups(@ApiParam(name = "limit", value = "limit", required = false, type = "Integer", example = "xxx")
                                          @RequestParam(value = "limit", required = false) Integer limit,
                                          @ApiParam(name = "offset", value = "offset", required = false, type = "Integer", example = "xxx")
                                          @RequestParam(value = "offset", required = false) Integer offset,
                                          HttpServletRequest request) throws ServiceException {

        if ((limit == null && offset != null) || (limit != null && offset == null)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR);
        }
        Integer currentUserId = getCurrentUserId(request);
        List<Groups> groupsList = userManager.getFavGroups(currentUserId, limit, offset);

        FavGroupsDTO favGroupsDTO = new FavGroupsDTO();
        favGroupsDTO.setLimit(limit);
        favGroupsDTO.setOffset(offset);
        favGroupsDTO.setTotal(groupsList.size());
        favGroupsDTO.setGroupsList(groupsList);
        return APIResultWrap.ok(favGroupsDTO);
    }


    @ApiOperation(value = "设置 SealTalk 号")
    @RequestMapping(value = "/set_st_account", method = RequestMethod.POST)
    public APIResult<Object> setStAccount(@ApiParam(name = "stAccount", value = "sealtalk 号", required = true, type = "String", example = "xxx")
                                          @RequestParam("stAccount") String stAccount,
                                          HttpServletRequest request) throws ServiceException {

        ValidateUtils.checkStAccount(stAccount);

        Integer currentUserId = getCurrentUserId(request);
        userManager.setStAccount(currentUserId, stAccount);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "设置性别")
    @RequestMapping(value = "/set_gender", method = RequestMethod.POST)
    public APIResult<Object> setGender(@ApiParam(name = "gender", value = "性别：男性 male 女性 female", required = true, type = "String", example = "xxx")
                                       @RequestParam("gender") String gender,
                                       HttpServletRequest request) throws ServiceException {

        ValidateUtils.checkGender(gender);
        Integer currentUserId = getCurrentUserId(request);
        Users u = new Users();
        u.setId(currentUserId);
        u.setGender(gender);

        userManager.updateUserById(u);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "设置个人隐私设置")
    @RequestMapping(value = "/set_privacy", method = RequestMethod.POST)
    public APIResult<Object> setPrivacy(@ApiParam(name = "phoneVerify", value = "是否允许通过手机号搜索到我", required = false, type = "Integer", example = "xxx")
                                        @RequestParam(value = "phoneVerify", required = false) Integer phoneVerify,
                                        @ApiParam(name = "stSearchVerify", value = "是否允许 SealTalk 号搜索到我", required = false, type = "Integer", example = "xxx")
                                        @RequestParam(value = "stSearchVerify", required = false) Integer stSearchVerify,
                                        @ApiParam(name = "friVerify", value = "是否加好友验证", required = false, type = "Integer", example = "xxx")
                                        @RequestParam(value = "friVerify", required = false) Integer friVerify,
                                        @ApiParam(name = "groupVerify", value = "是否允许直接添加至群聊", required = false, type = "Integer", example = "xxx")
                                        @RequestParam(value = "groupVerify", required = false) Integer groupVerify,
                                        HttpServletRequest request) throws ServiceException {

        ValidateUtils.checkPrivacy(phoneVerify, stSearchVerify, friVerify, groupVerify);
        Integer currentUserId = getCurrentUserId(request);

        Users users = userManager.getUser(currentUserId);

        Users u = new Users();
        u.setId(users.getId());
        u.setPhoneVerify(phoneVerify);
        u.setStSearchVerify(stSearchVerify);
        u.setFriVerify(friVerify);
        u.setGroupVerify(groupVerify);
        userManager.updateUserById(u);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取个人隐私设置")
    @RequestMapping(value = "/get_privacy", method = RequestMethod.GET)
    public APIResult<Object> getPrivacy(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        Users users = userManager.getUser(currentUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("phoneVerify", users.getPhoneVerify());
        result.put("stSearchVerify", users.getStSearchVerify());
        result.put("friVerify", users.getFriVerify());
        result.put("groupVerify", users.getGroupVerify());
        return APIResultWrap.ok(MiscUtils.encodeResults(result));
    }

    @ApiOperation(value = "设置接收戳一下消息状态")
    @RequestMapping(value = "/set_poke", method = RequestMethod.POST)
    public APIResult<Object> setPokeStatus(@ApiParam(name = "pokeStatus", value = "接收戳一下消息状态", required = true, type = "Integer", example = "xxx")
                                           @RequestParam("pokeStatus") Integer pokeStatus,
                                           HttpServletRequest request) throws ServiceException {

        ValidateUtils.checkPokeStatus(pokeStatus);

        Integer currentUserId = getCurrentUserId(request);
        Users u = new Users();
        u.setId(currentUserId);
        u.setPokeStatus(pokeStatus);
        userManager.updateUserById(u);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取接收戳一下消息状态")
    @RequestMapping(value = "/get_poke", method = RequestMethod.GET)
    public APIResult<Object> getPokeStatus(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        Users users = userManager.getUser(currentUserId);
        Map<String, Object> result = new HashMap<>();
        result.put("pokeStatus", users.getPokeStatus());
        return APIResultWrap.ok(MiscUtils.encodeResults(result));
    }


    /**
     * 设置AuthCookie
     *
     * @param response
     * @param userId
     */
    private void setCookie(HttpServletResponse response, int userId) {

        int salt = RandomUtil.randomBetween(1000, 9999);
        String text = salt+Constants.SEPARATOR_NO+userId+Constants.SEPARATOR_NO+System.currentTimeMillis();

        byte[] value = AES256.encrypt(text, sealtalkConfig.getAuthCookieKey());

        Cookie cookie = new Cookie(sealtalkConfig.getAuthCookieName(), new String(value));
        cookie.setHttpOnly(true);
        cookie.setDomain(sealtalkConfig.getAuthCookieDomain());
        cookie.setMaxAge(Integer.valueOf(sealtalkConfig.getAuthCookieMaxAge()));
        cookie.setPath("/");

        response.addCookie(cookie);
    }


}
