package com.final_team4.finalbe.auth.service;

import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.user.domain.Role;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.mapper.UserMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("DB에서 가입된 사용자 조회 후 로그인 성공")
    @Test
    void login_success() {
        // given
        String rawPassword = "password123!";
        User savedUser = insertUser("login-test@example.com", "로그인 테스트", rawPassword);
        LoginRequest request = new LoginRequest(savedUser.getEmail(), rawPassword);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.userId()).isEqualTo(savedUser.getId());
        assertThat(response.name()).isEqualTo(savedUser.getName());
        assertThat(response.role()).isEqualTo(savedUser.getRole().getName());
        assertThat(response.issuedAt()).isNotNull();
        assertThat(response.expiresAt()).isAfter(response.issuedAt());
    }

    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    @Test
    void login_fail_whenEmailNotFound() {
        // given
        LoginRequest request = new LoginRequest("unknown-user@example.com", "password123!");

        // when && then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("가입되지 않은 이메일입니다.");
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    void login_fail_whenPasswordDoesNotMatch() {
        // given
        String rawPassword = "password123!";
        User savedUser = insertUser("wrong-password@example.com", "비밀번호 검증", rawPassword);
        LoginRequest request = new LoginRequest(savedUser.getEmail(), "invalidPassword!");

        // when && then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    private User insertUser(String email, String name, String rawPassword) {
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.from(RoleType.USER);
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(rawPassword))
                .roleId(role.getId())
                .role(role)
                .isDelete(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        userMapper.insert(user);
        return user;
    }
}
