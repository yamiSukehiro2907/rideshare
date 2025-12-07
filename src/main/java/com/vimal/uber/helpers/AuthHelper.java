package com.vimal.uber.helpers;

import jakarta.servlet.http.Cookie;

public class AuthHelper {

    public static Cookie createAccessTokenCookie(String accessToken, int maxAge) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(maxAge);
        return accessCookie;
    }

    public static Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(maxAge);
        return refreshCookie;
    }
}