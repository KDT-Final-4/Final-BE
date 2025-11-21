package com.final_team4.finalbe.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserMapper userMapper;

    @MockitoBean
    UserInfoMapper userInfoMapper;

    @MockitoBean
    ScheduleMapper scheduleMapper;

    @DisplayName("회원가입 성공 시 ApiResponse 형태로 성공 메시지와 유저 요약 정보를 반환한다")
    @Test
    void register_success() throws Exception {
        // given
        UserSummaryResponse summary = UserSummaryResponse.builder()
                .userId(1L)
                .email("codex@example.com")
                .name("codex")
                .build();
        given(userService.register(any(UserRegisterRequestDto.class))).willReturn(summary);

        UserRegisterRequestDto request = UserRegisterRequestDto.builder()
                .email("codex@example.com")
                .password("pw1234!")
                .name("codex")
                .build();

        // when & then
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료됐습니다."))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value("codex@example.com"))
                .andExpect(jsonPath("$.data.name").value("codex"))
                .andExpect(jsonPath("$.timestamp").exists());

        ArgumentCaptor<UserRegisterRequestDto> captor = ArgumentCaptor.forClass(UserRegisterRequestDto.class);
        verify(userService).register(captor.capture());
        UserRegisterRequestDto captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo("codex@example.com");
        assertThat(captured.getPassword()).isEqualTo("pw1234!");
        assertThat(captured.getName()).isEqualTo("codex");
    }

    @DisplayName("회원가입 실패 - 필수 값이 없으면 400 Bad Request를 반환하고 서비스가 호출되지 않는다")
    @Test
    void register_validationError() throws Exception {
        // given
        String invalidPayload = """
                {
                    "email": "not-an-email",
                    "password": "",
                    "name": ""
                }
                """;

        // when & then
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
