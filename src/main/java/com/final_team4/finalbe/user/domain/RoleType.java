package com.final_team4.finalbe.user.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    ADMIN(1L, "ROLE_ADMIN", "Administrator role"),
    USER(2L, "ROLE_USER", "Default user role");

    private final Long id;
    private final String name;
    private final String description;

    RoleType(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static RoleType fromId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Role id cannot be null");
        }

        return Arrays.stream(values())
                .filter(type -> type.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role id: " + id));
    }
}
