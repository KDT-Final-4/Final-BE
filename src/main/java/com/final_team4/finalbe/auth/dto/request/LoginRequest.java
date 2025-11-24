package com.final_team4.finalbe.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email(message = "email must be valid")
    @NotBlank(message = "email must not be blank")
    String email,
    @NotBlank(message = "password must not be blank")
    String password
) {
}
