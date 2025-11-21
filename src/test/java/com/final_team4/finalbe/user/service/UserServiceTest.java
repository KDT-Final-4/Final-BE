package com.final_team4.finalbe.user.service;

import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private UserInfoMapper userInfoMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userMapper, userInfoMapper, passwordEncoder);
  }

  @DisplayName("회원가입 성공 - 비밀번호 암호화, 기본 역할(RoleType.USER) 설정, UserSummary 반환")
  @Test
  void register_success() {
    // given
    UserRegisterRequestDto request = UserRegisterRequestDto.builder()
        .email("codex@example.com")
        .password("plainPw123!")
        .name("codex")
        .build();

    given(userMapper.findByEmail(eq("codex@example.com"))).willReturn(null);
    given(passwordEncoder.encode("plainPw123!")).willReturn("encodedPw");

    UserSummaryResponse summaryResponse = UserSummaryResponse.builder()
        .userId(1L)
        .email("codex@example.com")
        .name("codex")
        .build();
    given(userInfoMapper.toUserSummary(any(User.class))).willReturn(summaryResponse);

    // when
    UserSummaryResponse result = userService.register(request);

    // then
    assertThat(result).isSameAs(summaryResponse);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userMapper).insert(userCaptor.capture());
    User saved = userCaptor.getValue();

    assertThat(saved.getEmail()).isEqualTo("codex@example.com");
    assertThat(saved.getName()).isEqualTo("codex");
    assertThat(saved.getPassword()).isEqualTo("encodedPw");
    assertThat(saved.getRoleId()).isEqualTo(RoleType.USER.getId());
    assertThat(saved.getIsDelete()).isZero();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  @DisplayName("회원가입 실패 - 이메일 중복 시 예외 발생")
  @Test
  void register_duplicateEmail_throwsException() {
    // given
    UserRegisterRequestDto request = UserRegisterRequestDto.builder()
        .email("dup@example.com")
        .password("pw")
        .name("dup")
        .build();

    given(userMapper.findByEmail("dup@example.com")).willReturn(new User());

    // when & then
    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("dup@example.com");
  }
}
