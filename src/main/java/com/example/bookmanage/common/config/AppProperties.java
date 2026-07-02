package com.example.bookmanage.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Api api = new Api();
    private Auth auth = new Auth();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Api {

        private String traceHeaderName = "X-Trace-Id";

        private String systemErrorMessage = "系统异常，请稍后再试";

        public String getTraceHeaderName() {
            return traceHeaderName;
        }

        public void setTraceHeaderName(String traceHeaderName) {
            this.traceHeaderName = traceHeaderName;
        }

        public String getSystemErrorMessage() {
            return systemErrorMessage;
        }

        public void setSystemErrorMessage(String systemErrorMessage) {
            this.systemErrorMessage = systemErrorMessage;
        }
    }

    public static class Auth {

        private String secret = "book-manage-dev-secret";

        private Long expireSeconds = 7200L;

        private String tokenHeader = "Authorization";

        private String tokenPrefix = "Bearer ";

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getExpireSeconds() {
            return expireSeconds;
        }

        public void setExpireSeconds(Long expireSeconds) {
            this.expireSeconds = expireSeconds;
        }

        public String getTokenHeader() {
            return tokenHeader;
        }

        public void setTokenHeader(String tokenHeader) {
            this.tokenHeader = tokenHeader;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }
    }
}
