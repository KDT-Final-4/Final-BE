package com.final_team4.finalbe.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    private Long id;

    private String name;

    private String description;

    public static Role from(RoleType roleType) {
        if (roleType == null) {
            return null;
        }

        return Role.builder()
            .id(roleType.getId())
            .name(roleType.getName())
            .description(roleType.getDescription())
            .build();
    }

    public static Role fromId(Long roleId) {
        if (roleId == null) {
            return null;
        }

        return from(RoleType.fromId(roleId));
    }

    public static Long defaultRoleId() {
        return RoleType.USER.getId();
    }

}
