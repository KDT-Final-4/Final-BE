package com.final_team4.finalbe.user.service;

import com.final_team4.finalbe._core.exception.DuplicateEmailException;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserMapper;
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
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @DisplayName("회원가입 성공 - DB에 저장되고 암호화된 비밀번호와 기본 역할이 설정된다")
  @Test
  void register_success() {
    // given
    UserRegisterRequestDto request = registerRequest("codex@example.com", "plainPw123!", "codex");

    // when
    UserSummaryResponse result = userService.register(request);
    User saved = userMapper.findByEmail(request.getEmail());

    // then
    assertThat(result.getUserId()).isEqualTo(saved.getId());
    assertThat(result.getEmail()).isEqualTo(saved.getEmail());
    assertThat(result.getName()).isEqualTo(saved.getName());

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("codex");
    assertThat(saved.getEmail()).isEqualTo("codex@example.com");
    assertThat(passwordEncoder.matches(request.getPassword(), saved.getPassword())).isTrue();
    assertThat(saved.getRoleId()).isEqualTo(RoleType.USER.getId());
    assertThat(saved.getIsDelete()).isZero();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  @DisplayName("회원가입 실패 - 이메일 중복 시 예외 발생")
  @Test
  void register_duplicateEmail_throwsException() {
    // given
    UserRegisterRequestDto request = registerRequest("dup@example.com", "pw123!", "dup");
    userService.register(request);

    // when & then
    assertThatThrownBy(() -> userService.register(registerRequest("dup@example.com", "pw123!", "dup")))
        .isInstanceOf(DuplicateEmailException.class)
        .hasMessageContaining("dup@example.com");
  }

  private UserRegisterRequestDto registerRequest(String email, String password, String name) {
    return UserRegisterRequestDto.builder()
        .email(email)
        .password(password)
        .name(name)
        .build();
  }
}
