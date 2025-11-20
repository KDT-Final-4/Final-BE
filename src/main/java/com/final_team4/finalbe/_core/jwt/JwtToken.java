package com.final_team4.finalbe._core.jwt;

import java.time.Instant;
import java.util.List;

public record JwtToken(String value, Instant issuedAt, List<String> roles) {
}
