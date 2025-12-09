package com.final_team4.finalbe.user.service;

import com.final_team4.finalbe._core.exception.BadRequestException;
import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.DuplicateEmailException;
import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import com.final_team4.finalbe.setting.mapper.llm.LlmChannelMapper;
import com.final_team4.finalbe.user.domain.RoleType;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.PasswordUpdateRequest;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.UserUpdateRequest;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private ScheduleSettingMapper scheduleSettingMapper;

    @Autowired
    private LlmChannelMapper llmChannelMapper;

    @DisplayName("회원가입 성공 - DB에 저장되고 암호화된 비밀번호와 기본 역할이 설정된다")
    @Test
    void register_success() {
        // given
        UserRegisterRequestDto request = registerRequest("codex@example.com", "plainPw123!", "codex");

        // when
        UserSummaryResponse result = userService.register(request);
        User saved = userMapper.findByEmail(request.getEmail());
        ScheduleSetting defaultSchedule = scheduleSettingMapper.findByUserId(saved.getId());
        LlmChannel defaultChannel = llmChannelMapper.findByUserId(saved.getId());

        // then
        assertThat(result.getUserId()).isEqualTo(saved.getId());
        assertThat(result.getEmail()).isEqualTo(saved.getEmail());
        assertThat(result.getName()).isEqualTo(saved.getName());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("codex");
        assertThat(saved.getEmail()).isEqualTo("codex@example.com");
        assertThat(passwordEncoder.matches(request.getPassword(), saved.getPassword())).isTrue();
        assertThat(saved.getRoleId()).isEqualTo(RoleType.USER.getId());
        assertThat(saved.getIsDelete()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        assertThat(defaultSchedule).isNotNull();
        assertThat(defaultSchedule.isRun()).isFalse();
        assertThat(defaultSchedule.getMaxDailyRuns()).isZero();
        assertThat(defaultSchedule.getRetryOnFail()).isZero();

        assertThat(defaultChannel).isNotNull();
        assertThat(defaultChannel.getStatus()).isFalse();
        assertThat(defaultChannel.getName()).isEqualTo("openAi");
        assertThat(defaultChannel.getModelName()).isEqualTo("gpt5");
        assertThat(defaultChannel.getApiKey()).isEqualTo("");
        assertThat(defaultChannel.getMaxTokens()).isEqualTo(2000);
        assertThat(defaultChannel.getTemperature()).isEqualByComparingTo("0.9");
        assertThat(defaultChannel.getPrompt()).isNull();
        assertThat(defaultChannel.getGenerationType()).isEqualTo(ContentGenType.AUTO);
    }

    @DisplayName("회원가입 실패 - 이메일 중복 시 예외 발생")
    @Test
    void register_duplicateEmail_throwsException() {
        // given
        UserRegisterRequestDto request = registerRequest("dup@example.com", "pw123456!", "dup");
        userService.register(request);

        // when & then
        assertThatThrownBy(() -> userService.register(registerRequest("dup@example.com", "pw123456!", "dup")))
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

    @DisplayName("프로필 조회 실패 - 없는 사용자는 예외")
    @Test
    void findProfile_notFound() {

        assertThatThrownBy(() -> userService.findProfile(999L))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");

    }

    @DisplayName("프로필 수정 성공 - 이름만 변경된다")
    @Test
    void updateProfile_success() {

        UserSummaryResponse saved = userService.register(registerRequest(
                "update@example.com",
                "pw123456!",
                "old"));

        UserUpdateRequest request = UserUpdateRequest.builder().name("newName").build();

        User updated = userService.updateProfile(saved.getUserId(), request);

        assertThat(updated.getName()).isEqualTo("newName");
        assertThat(userMapper.findAvailableById(saved.getUserId()).getName()).isEqualTo("newName");
    }

    @DisplayName("비밀번호 변경 성공 - 기존 비밀번호 검증 후 새 비밀번호 저장")
    @Test
    void updatePassword_success() {
        UserSummaryResponse saved = userService.register(registerRequest(
                "pw@example.com",
                "oldPass!23",
                "pw"));

        JwtPrincipal principal = JwtPrincipal.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .name(saved.getName())
                .role("ROLE_USER")
                .authorities(List.of())
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true)
                .build();

        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .oldPassword("oldPass!23")
                .newPassword("newPass!23")
                .confirmNewPassword("newPass!23")
                .build();

        userService.updatePassword(request, principal.userId());

        User updated = userMapper.findById(saved.getUserId());
        assertThat(passwordEncoder.matches("newPass!23", updated.getPassword())).isTrue();

    }

    @DisplayName("비밀번호 변경 실패 - 기존 비밀번호가 틀리면 401")
    @Test
    void updatePassword_wrongOldPassword() {

        UserSummaryResponse saved = userService.register(registerRequest("wrongold@example.com",
                "oldPass!23",
                "pw")
        );
        JwtPrincipal principal = JwtPrincipal.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .name(saved.getName())
                .role("ROLE_USER")
                .authorities(List.of())
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true)
                .build();

        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .oldPassword("badOld!23")
                .newPassword("newPass!23")
                .confirmNewPassword("newPass!23")
                .build();

        assertThatThrownBy(() -> userService.updatePassword(request, principal.userId()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }


    @DisplayName("비밀번호 변경 실패 - 새 비밀번호와 확인 불일치")
    @Test
    void updatePassword_confirmMismatch() {

        UserSummaryResponse saved = userService.register(registerRequest("confirm@example.com",
                "oldPass!23",
                "pw")
        );

        JwtPrincipal principal = JwtPrincipal.builder()
                .userId(saved.getUserId())
                .email(saved.getEmail())
                .name(saved.getName())
                .role("ROLE_USER")
                .authorities(List.of())
                .accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).enabled(true)
                .build();

        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .oldPassword("oldPass!23")
                .newPassword("newPass!23")
                .confirmNewPassword("different!23")
                .build();

        assertThatThrownBy(() -> userService.updatePassword(request, principal.userId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("비밀번호 확인");
    }


}
