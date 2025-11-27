package com.final_team4.finalbe.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    @Size(min =8,max = 14, message = "비밀번호는 8자 이상 14자 이하이어야 합니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 14, message = "비밀번호는 8자 이상 14자 이하이어야 합니다.")
    private String newPassword;

    @NotBlank(message = "확인용 비밀번호는 필수입니다.")
    @Size(min = 8, max = 14, message = "비밀번호는 8자 이상 14자 이하이어야 합니다.")
    private String confirmNewPassword;


}
