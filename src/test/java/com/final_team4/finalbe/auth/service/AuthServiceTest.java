package com.final_team4.finalbe.auth.service;

import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @DisplayName("DB에서 가입된 사용자 조회 후 로그인 성공")
    @Test
    void login_success() {
        // given
        String rawPassword = "password123!";
        User savedUser = registerUser("login-test@example.com", "로그인 테스트", rawPassword);
        LoginRequest request = new LoginRequest(savedUser.getEmail(), rawPassword);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.userId()).isEqualTo(savedUser.getId());
        assertThat(response.name()).isEqualTo(savedUser.getName());
        assertThat(response.role()).isEqualTo(savedUser.getRole().getName());
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
        User savedUser = registerUser("wrong-password@example.com", "비밀번호 검증", rawPassword);
        LoginRequest request = new LoginRequest(savedUser.getEmail(), "invalidPassword!");

        // when && then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    private User registerUser(String email, String name, String rawPassword) {
        UserRegisterRequestDto request = UserRegisterRequestDto.builder()
                .email(email)
                .password(rawPassword)
                .name(name)
                .build();
        userService.register(request);
        return userMapper.findByEmail(email);
    }
}
