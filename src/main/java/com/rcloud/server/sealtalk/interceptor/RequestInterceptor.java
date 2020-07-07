package com.rcloud.server.sealtalk.interceptor;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.ServerApiCookie;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.util.AES256;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    public static final String REGEX = "|";
    @Resource
    private SealtalkConfig sealtalkConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        Integer currentUserId = null;
        Cookie authCookie = getAuthCookie(request);
        if (authCookie != null) {
            currentUserId = getCurrentUserId(authCookie);
        }
        ServerApiCookie serverApiCookie = new ServerApiCookie();
        serverApiCookie.setCurrentUserId(currentUserId);
        ServerApiParams serverApiParams = new ServerApiParams();
        serverApiParams.setServerApiCookie(serverApiCookie);
        request.getSession().setAttribute(Constants.SERVER_API_PARAMS, serverApiParams);
        return true;
    }

    private Integer getCurrentUserId(Cookie authCookie) throws ServiceException {
        String cookieValue = authCookie.getValue();
        String decrypt = AES256.decrypt(cookieValue.getBytes(), sealtalkConfig.getAuthCookieKey());
        assert decrypt != null;
        String[] split = decrypt.split(REGEX);
        if (split.length != 3) {
            throw new ServiceException(ErrorCode.COOKIE_ERROR, "Invalid cookie value!");
        }
        return Integer.parseInt(split[1]);
    }

    private Cookie getAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(sealtalkConfig.getAuthCookieName())) {
                return cookie;
            }
        }
        return null;
    }


}
