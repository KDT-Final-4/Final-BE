package com.final_team4.finalbe.auth.dto.response;

import java.time.Instant;

public record LoginResponse(
    String accessToken,
    Instant issuedAt,
    Instant expiresAt,
    Long userId,
    String name,
    String role
) {
}
