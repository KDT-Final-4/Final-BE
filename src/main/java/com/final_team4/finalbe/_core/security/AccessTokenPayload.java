package com.final_team4.finalbe._core.security;

import com.final_team4.finalbe._core.jwt.JwtToken;

import java.time.Instant;

public record AccessTokenPayload(String accessToken, Instant issuedAt, Instant expiresAt) {

    public static AccessTokenPayload from(JwtToken token) {
        return new AccessTokenPayload(token.value(), token.issuedAt(), token.expiresAt());
    }


}
