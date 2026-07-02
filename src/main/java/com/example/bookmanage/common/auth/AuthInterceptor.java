package com.example.bookmanage.common.auth;

import com.example.bookmanage.common.config.AppProperties;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 登录 token 全局拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;
    private final AuthTokenUtil authTokenUtil;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(AppProperties appProperties, AuthTokenUtil authTokenUtil, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.authTokenUtil = authTokenUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = resolveToken(request);
        AuthUser authUser = authTokenUtil.parseToken(token);
        if (authUser == null) {
            writeUnauthorized(response);
            return false;
        }

        AuthContext.setCurrentUser(authUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String headerValue = request.getHeader(appProperties.getAuth().getTokenHeader());
        String tokenPrefix = appProperties.getAuth().getTokenPrefix();
        if (!StringUtils.hasText(headerValue) || !headerValue.startsWith(tokenPrefix)) {
            return null;
        }
        return headerValue.substring(tokenPrefix.length());
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(ErrorCode.UNAUTHORIZED)));
    }
}
