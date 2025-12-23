package com.final_team4.finalbe.auth.dto.info;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class TokenInfo {
    private String token;
    private Instant issuedAt;
    private List<String> roles;
}