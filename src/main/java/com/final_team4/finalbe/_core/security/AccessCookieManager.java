package com.final_team4.finalbe._core.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AccessCookieManager {

    @Value("${cookie.secure:false}") // 기본값 false
    private boolean cookieSecure;


    private static final List<String> ACCESS_COOKIES =
            List.of("ACCESS_TOKEN", "ACCESS_EXPIRES_AT", "ACCESS_ISSUED_AT");


    public void clearAccessCookies(HttpServletResponse response) {
        ACCESS_COOKIES.forEach(name -> addExpired(response, name));
    }

    private String cookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true).secure(cookieSecure).path("/")
                .sameSite("Lax").maxAge(maxAge).build().toString();
    }

    private void addExpired(HttpServletResponse response, String name) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from(name, "")
                        .httpOnly(true).secure(cookieSecure)
                        .path("/").sameSite("Lax").maxAge(0).build().toString());
    }

    public void setAccessCookies(HttpServletResponse response, AccessTokenPayload payload) {
        Duration ttl = Duration.between(payload.issuedAt(), payload.expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookie("ACCESS_TOKEN", payload.accessToken(), ttl));
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookie("ACCESS_ISSUED_AT", payload.issuedAt().toString(), ttl));
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookie("ACCESS_EXPIRES_AT", payload.expiresAt().toString(), ttl));
    }



}
