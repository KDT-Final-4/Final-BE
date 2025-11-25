package com.final_team4.finalbe.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Schema(example = "user@example.com")
    @Email(message = "email must be valid")
    @NotBlank(message = "email must not be blank")
    String email,
    @Schema(example = "P@ssw0rd!")
    @NotBlank(message = "password must not be blank")
    String password
) {
}
