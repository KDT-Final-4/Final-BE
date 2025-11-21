package com.final_team4.finalbe.user.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

public class User {

    private Long id;

    private Long roleId;

    private Role role;

    private String name;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer deleted;

    private String prompt;

    private String apiKey;

    private String llmPublisher;

    private String llmModel;

    private Integer targetTextLength;

    public Role getRole() {
        if (role == null && roleId != null) {
            this.role = Role.fromId(roleId);
        }

        return role;
    }

    public void assignRole(Role role) {
        this.role = role;
        this.roleId = role != null ? role.getId() : null;
    }

    public void assignRole(RoleType roleType) {
        assignRole(Role.from(roleType));
    }
}
