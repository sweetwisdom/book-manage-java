package com.example.bookmanage.common.auth;

import com.example.bookmanage.common.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AuthInterceptorTest {

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void preHandle_ShouldRejectRequest_WhenTokenMissing() throws Exception {
        AuthInterceptor authInterceptor = createAuthInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("未登录或登录已过期");
    }

    @Test
    void preHandle_ShouldSetCurrentUser_WhenTokenValid() throws Exception {
        AppProperties appProperties = createAppProperties();
        AuthTokenUtil authTokenUtil = new AuthTokenUtil(appProperties);
        AuthInterceptor authInterceptor = new AuthInterceptor(appProperties, authTokenUtil, new ObjectMapper());
        String token = authTokenUtil.createToken(new AuthUser(1L, "admin", "ADMIN"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(AuthContext.getCurrentUser()).isNotNull();
        assertThat(AuthContext.getCurrentUser().getUserId()).isEqualTo(1L);
    }

    private AuthInterceptor createAuthInterceptor() {
        AppProperties appProperties = createAppProperties();
        AuthTokenUtil authTokenUtil = new AuthTokenUtil(appProperties);
        return new AuthInterceptor(appProperties, authTokenUtil, new ObjectMapper());
    }

    private AppProperties createAppProperties() {
        AppProperties appProperties = new AppProperties();
        appProperties.getAuth().setSecret("test-secret");
        appProperties.getAuth().setExpireSeconds(7200L);
        return appProperties;
    }
}
