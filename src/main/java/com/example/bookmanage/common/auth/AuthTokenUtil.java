package com.example.bookmanage.common.auth;

import com.example.bookmanage.common.config.AppProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT token 生成与校验工具
 */
@Component
public class AuthTokenUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public AuthTokenUtil(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * 生成token
     * @param authUser
     * @return
     */
    public String createToken(AuthUser authUser) {
        long now = Instant.now().getEpochSecond();
        long expireSeconds = appProperties.getAuth().getExpireSeconds();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", authUser.getUserId());
        payload.put("name", authUser.getName());
        payload.put("role", authUser.getRole());
        payload.put("iat", now);
        payload.put("exp", now + expireSeconds);

        String headerText = base64UrlEncode(toJsonBytes(header));
        String payloadText = base64UrlEncode(toJsonBytes(payload));
        String unsignedToken = headerText + "." + payloadText;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public AuthUser parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            return null;
        }

        Map<String, Object> payload = fromJsonBytes(base64UrlDecode(parts[1]));
        Long expireAt = toLong(payload.get("exp"));
        if (expireAt == null || expireAt < Instant.now().getEpochSecond()) {
            return null;
        }

        Long userId = toLong(payload.get("userId"));
        String name = toString(payload.get("name"));
        String role = toString(payload.get("role"));
        if (userId == null || !StringUtils.hasText(name)) {
            return null;
        }
        return new AuthUser(userId, name, role);
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(appProperties.getAuth().getSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return base64UrlEncode(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("生成 token 签名失败", e);
        }
    }

    private byte[] toJsonBytes(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new IllegalStateException("序列化 token 失败", e);
        }
    }

    private Map<String, Object> fromJsonBytes(byte[] data) {
        try {
            return objectMapper.readValue(data, MAP_TYPE);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private byte[] base64UrlDecode(String data) {
        try {
            return Base64.getUrlDecoder().decode(data);
        } catch (IllegalArgumentException e) {
            return new byte[0];
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
}
