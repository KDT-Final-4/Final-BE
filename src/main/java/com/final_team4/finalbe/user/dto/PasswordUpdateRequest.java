package com.final_team4.finalbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

    private String oldPassword;

    private String newPassword;
    private String confirmNewPassword;


}
