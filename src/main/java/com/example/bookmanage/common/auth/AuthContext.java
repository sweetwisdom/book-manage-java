package com.example.bookmanage.common.auth;

/**
 * 请求级认证上下文
 */
public final class AuthContext {

    private static final ThreadLocal<AuthUser> CURRENT_USER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setCurrentUser(AuthUser authUser) {
        CURRENT_USER.set(authUser);
    }

    public static AuthUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
