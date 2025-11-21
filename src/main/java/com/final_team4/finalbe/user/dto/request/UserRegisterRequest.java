package com.final_team4.finalbe.user.dto.request;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String password;

    @NotBlank
    private String name;

    @Builder.Default
    private Long roleId = Role.defaultRoleId();

    public User toEntity() {
        LocalDateTime now = LocalDateTime.now();
        RoleType resolvedRoleType = resolveRoleType();
        Role resolvedRole = Role.from(resolvedRoleType);

        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .roleId(resolvedRole.getId())
                .role(resolvedRole)
                .createdAt(now)
                .updatedAt(now)
                .deleted(0)
                .prompt("아직 미정")
                .llmPublisher("아직 미정")
                .build();
    }

    private RoleType resolveRoleType() {
        Long requestedRoleId = roleId != null ? roleId : Role.defaultRoleId();
        return RoleType.fromId(requestedRoleId);
    }
}
