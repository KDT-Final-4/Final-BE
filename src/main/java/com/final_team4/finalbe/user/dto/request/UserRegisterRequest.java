package com.final_team4.finalbe.user.dto.request;

import com.final_team4.finalbe.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRegisterRequest {
    private String email;
    private String password;
    private String name;

    public User toEntity() {
        LocalDateTime now = LocalDateTime.now();

        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .deleted(0)
                .build();
    }
}
