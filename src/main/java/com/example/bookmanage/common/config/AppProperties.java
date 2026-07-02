package com.example.bookmanage.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Api api = new Api();
    private Auth auth = new Auth();

    @Data
    public static class Api {

        private String traceHeaderName = "X-Trace-Id";

        private String systemErrorMessage = "系统异常，请稍后再试";
    }

    @Data
    public static class Auth {

        private String secret = "book-manage-dev-secret";

        private Long expireSeconds = 7200L;

        private String tokenHeader = "Authorization";

        private String tokenPrefix = "Bearer ";
    }
}
