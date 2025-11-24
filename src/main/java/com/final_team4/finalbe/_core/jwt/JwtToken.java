package com.final_team4.finalbe._core.jwt;

import java.time.Instant;
import java.util.List;

public record JwtToken(
    String value,
    Instant issuedAt,
    Instant expiresAt,
    Long userId,
    String name,
    String role,
    List<String> roles) {
}
