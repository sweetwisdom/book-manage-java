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

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
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
}
