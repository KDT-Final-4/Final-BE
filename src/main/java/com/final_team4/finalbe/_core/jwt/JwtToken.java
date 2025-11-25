package com.final_team4.finalbe._core.jwt;

import java.time.Instant;

public record JwtToken(
    String value,
    Instant issuedAt,
    Instant expiresAt,
    Long userId,
    String name,
    String role) {
}
