package com.example.bookmanage.common.auth;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.RegisteredPayload;
import com.example.bookmanage.common.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT token 生成与校验工具
 */
@Component
public class AuthTokenUtil {

    private final AppProperties appProperties;

    public AuthTokenUtil(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * 生成 token
     */
    public String createToken(AuthUser authUser) {
        Instant now = Instant.now();
        long expireSeconds = appProperties.getAuth().getExpireSeconds();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", authUser.getUserId());
        payload.put("name", authUser.getName());
        payload.put("role", authUser.getRole());
        payload.put(RegisteredPayload.ISSUED_AT, Date.from(now));
        payload.put(RegisteredPayload.EXPIRES_AT, Date.from(now.plusSeconds(expireSeconds)));

        return JWTUtil.createToken(payload, secretBytes());
    }

    public AuthUser parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        try {
            if (!JWTUtil.verify(token, secretBytes())) {
                return null;
            }

            JWT jwt = JWTUtil.parseToken(token);
            Long expireAt = toLong(jwt.getPayload(RegisteredPayload.EXPIRES_AT));
            if (expireAt == null || expireAt < Instant.now().getEpochSecond()) {
                return null;
            }

            Long userId = toLong(jwt.getPayload("userId"));
            String name = toString(jwt.getPayload("name"));
            String role = toString(jwt.getPayload("role"));
            if (userId == null || !StringUtils.hasText(name)) {
                return null;
            }
            return new AuthUser(userId, name, role);
        } catch (Exception e) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private byte[] secretBytes() {
        return appProperties.getAuth().getSecret().getBytes(StandardCharsets.UTF_8);
    }
}
