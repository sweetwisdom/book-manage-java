package com.example.bookmanage.common.auth;

import com.example.bookmanage.common.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenUtilTest {

    @Test
    void parseToken_ShouldReturnAuthUser_WhenTokenValid() {
        AuthTokenUtil authTokenUtil = createAuthTokenUtil(7200L);
        String token = authTokenUtil.createToken(new AuthUser(1L, "admin", "ADMIN"));

        AuthUser authUser = authTokenUtil.parseToken(token);

        assertThat(authUser).isNotNull();
        assertThat(authUser.getUserId()).isEqualTo(1L);
        assertThat(authUser.getName()).isEqualTo("admin");
        assertThat(authUser.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void parseToken_ShouldReturnNull_WhenTokenTampered() {
        AuthTokenUtil authTokenUtil = createAuthTokenUtil(7200L);
        String token = authTokenUtil.createToken(new AuthUser(1L, "admin", "ADMIN"));

        AuthUser authUser = authTokenUtil.parseToken(token + "tampered");

        assertThat(authUser).isNull();
    }

    @Test
    void parseToken_ShouldReturnNull_WhenTokenExpired() throws Exception {
        AuthTokenUtil authTokenUtil = createAuthTokenUtil(0L);
        String token = authTokenUtil.createToken(new AuthUser(1L, "admin", "ADMIN"));
        Thread.sleep(1100L);

        AuthUser authUser = authTokenUtil.parseToken(token);

        assertThat(authUser).isNull();
    }

    private AuthTokenUtil createAuthTokenUtil(Long expireSeconds) {
        AppProperties appProperties = new AppProperties();
        appProperties.getAuth().setSecret("test-secret");
        appProperties.getAuth().setExpireSeconds(expireSeconds);
        return new AuthTokenUtil(appProperties, new ObjectMapper());
    }
}
