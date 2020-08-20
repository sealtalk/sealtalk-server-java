package com.rcloud.server.sealtalk.interceptor;

import com.github.pagehelper.util.StringUtil;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.RequestUriInfo;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.util.AES256;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.N3d;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String UNKNOWN = "unknown";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    public static final Set<String> excludeUrlSet = new CopyOnWriteArraySet<String>();

    @Resource
    private SealtalkConfig sealtalkConfig;

    @PostConstruct
    public void postConstruct() {
        String excludeUrls = sealtalkConfig.getExcludeUrl();
        if (!StringUtils.isEmpty(excludeUrls)) {
            String[] excludeUrlArray = excludeUrls.split(",");
            for (String excludeUrl : excludeUrlArray) {
                excludeUrlSet.add(excludeUrl.trim());
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        ServerApiParams serverApiParams = new ServerApiParams();
        serverApiParams.setTraceId(UUID.randomUUID().toString());
        String uri = request.getRequestURI();

        String userAgent = request.getHeader("User-Agent");
        if (userAgent.length() > 50) {
            userAgent = userAgent.substring(50);
        }
        String method = request.getMethod();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Map<String, String[]> tempParameterMap = new HashMap<>(parameterMap);
        RequestUriInfo requestUriInfo = getRequestUriInfo(request);
        serverApiParams.setRequestUriInfo(requestUriInfo);

        if (excludeUrlSet.contains(uri)) {
            //排除auth认证的url
            if (parameterMap.containsKey("password")) {
                tempParameterMap.put("password", new String[]{"**********"});
            }
            log.info("requtest info: userAgent={},method={},uri={},parameters={},traceId={} ", userAgent, method, uri, JacksonUtil.toJson(tempParameterMap),serverApiParams.getTraceId());
        } else {
            Cookie authCookie = getAuthCookie(request);
            if (authCookie == null) {
                response.setStatus(403);
                response.getWriter().write("Not loged in.");
                return false;
            }

            Integer currentUserId = null;
            try {
                currentUserId = getCurrentUserId(authCookie);
                serverApiParams.setCurrentUserId(currentUserId);
            } catch (Exception e) {
                log.error("获取currentUserId异常,error: " + e.getMessage(), e);
            }

            if (currentUserId == null) {
                response.setStatus(500);
                response.getWriter().write("Invalid cookie value");
                return false;
            }
            log.info("requtest info: userAgent={},method={},uri={},parameters={},traceId={},{} ", userAgent,  method, uri, JacksonUtil.toJson(tempParameterMap),serverApiParams.getTraceId(),N3d.encode(currentUserId));

        }
        ServerApiParamHolder.put(serverApiParams);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ServerApiParamHolder.remove();
    }

    private Integer getCurrentUserId(Cookie authCookie) throws ServiceException {
        String cookieValue = authCookie.getValue();
        String decrypt = AES256.decrypt(cookieValue.getBytes(), sealtalkConfig.getAuthCookieKey());
        assert decrypt != null;
        String[] split = decrypt.split(Constants.SEPARATOR_ESCAPE);
        if (split.length != 3) {
            throw new ServiceException(ErrorCode.COOKIE_ERROR, "Invalid cookie value!");
        }
        return Integer.parseInt(split[1]);
    }

    private Cookie getAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
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
