package com.rcloud.server.sealtalk.configuration;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.HttpStatusCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.interceptor.ServerApiParamHolder;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RestController
@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    private static final String CHARSET = "UTF-8";

    @ExceptionHandler(value = ServiceException.class)
    public void serviceAPIExceptionHandler(HttpServletRequest request, HttpServletResponse response,
                                           ServiceException e) throws Exception {
        String url = request.getRequestURI();
        String errorInfo = String.format("Error found: url:[%s],traceId:[%s],uid=[%s] ",url, ServerApiParamHolder.getTraceId(),ServerApiParamHolder.getEncodedCurrentUserId());

        log.error(errorInfo,e);

        String contentType = "application/json;charset=" + CHARSET;
        response.addHeader("Content-Type", contentType);

        if (!HttpStatusCode.CODE_200.getCode().equals(e.getHttpStatusCode())) {
            response.setStatus(e.getHttpStatusCode());
            response.getWriter().write(e.getMessage());
        } else {
            response.setStatus(HttpStatusCode.CODE_200.getCode());
            response.getWriter().write(JacksonUtil.toJson(APIResultWrap.error(e)));
        }
    }

    /**
     * ValidLocation 验证参数异常
     */
    @ExceptionHandler(value = BindException.class)
    public APIResult bindExceptionHandler(HttpServletRequest request, BindException e) {
        log.error("Error found:", e);
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> objectErrors = bindingResult.getAllErrors();
        ObjectError objectError = objectErrors.get(0);
        String errorMsg = objectError.getDefaultMessage();
        return APIResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public APIResult methodArgumentTypeExceptionHandler(HttpServletRequest request,
                                                        MethodArgumentTypeMismatchException e) {
        log.error("Error found:", e);
        String parameter = e.getName();
        String errorMsg = String.format("Argument %s type mismatch!", parameter);
        return APIResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }

    /**
     * 参数必传异常
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public APIResult missingServletRequestParameterExceptionHandler(
            HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error("Error found:", e);
        String parameter = e.getParameterName();
        String errorMsg = String.format("The parameter %s is required.", parameter);
        return APIResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }
}
