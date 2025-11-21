package com.final_team4.finalbe.auth.dto.response;

import java.time.Instant;
import java.util.List;

public record TokenResponse(String token, Instant issuedAt, List<String> roles) {
}
