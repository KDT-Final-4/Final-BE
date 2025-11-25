package com.final_team4.finalbe.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record LoginResponse(

    @JsonIgnore String accessToken,
    @JsonIgnore Instant issuedAt,
    @JsonIgnore Instant expiresAt,
    Long userId,
    String name,
    String role
) {
}
