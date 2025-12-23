package com.final_team4.finalbe.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.final_team4.finalbe._core.jwt.JwtToken;

import java.time.Instant;

public record LoginResponse(

    @JsonIgnore JwtToken accessToken,
    @JsonIgnore Instant issuedAt,
    @JsonIgnore Instant expiresAt,
    Long userId,
    String name,
    String role
) {
}
