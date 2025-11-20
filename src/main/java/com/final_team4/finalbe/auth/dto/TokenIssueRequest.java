package com.final_team4.finalbe.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(
    description = "테스트용 무기한 토큰 발급 요청",
    example = """
        {
          "username": "Test",
          "roles": ["ROLE_ADMIN"]
        }
        """
)
public record TokenIssueRequest(
    @NotBlank(message = "username must not be blank")
    String username,
    @Schema(description = "Optional role overrides", example = "[\"ROLE_ADMIN\"]")
    List<String> roles
) {
}
