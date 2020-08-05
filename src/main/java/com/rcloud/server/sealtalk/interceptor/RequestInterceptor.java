package com.rcloud.server.sealtalk.interceptor;

import com.github.pagehelper.util.StringUtil;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.RequestUriInfo;
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
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String UNKNOWN = "unknown";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

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
        RequestUriInfo requestUriInfo = getRequestUriInfo(request);
        serverApiParams.setRequestUriInfo(requestUriInfo);
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
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(sealtalkConfig.getAuthCookieName())) {
                    return cookie;
                }
            }
        }

        return null;
    }

    protected RequestUriInfo getRequestUriInfo(HttpServletRequest request) {
        String ip = getIpAddress(request);
        ip = StringUtil.isEmpty(ip) ? "" : ip;
        String uri = request.getRequestURI();
        String remoteAddress = request.getRemoteAddr();
        RequestUriInfo requestUriInfo = new RequestUriInfo();
        requestUriInfo.setUri(uri);
        requestUriInfo.setRemoteAddress(remoteAddress);
        requestUriInfo.setIp(ip);
        return requestUriInfo;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
