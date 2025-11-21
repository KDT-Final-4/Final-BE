package com.final_team4.finalbe.user.dto;

import com.final_team4.finalbe.user.domain.Role;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String password;

    @NotBlank
    private String name;


    public User toEntity() {
        LocalDateTime now = LocalDateTime.now();
        RoleType roleType = RoleType.USER; // 항상 기본 역할
        Role role = Role.from(roleType);

        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .roleId(role.getId())   // 1 고정
                .role(role)
                .createdAt(now)
                .updatedAt(now)
                .isDelete(0)
                .build();
    }


}
