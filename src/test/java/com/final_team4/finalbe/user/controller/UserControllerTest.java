package com.final_team4.finalbe.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.logger.aop.Loggable;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.service.UserService;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.HttpHeaders;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                MybatisAutoConfiguration.class // 자동으로 MyBatis띄우지 않도록 함
        }
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

    @MockitoBean
    ScheduleSettingMapper scheduleSettingMapper;

    @MockitoBean
    TrendMapper trendMapper;

    @MockitoBean
    UploadChannelMapper uploadChannelMapper;

    @MockitoBean
    Loggable loggable;

    @MockitoBean
    LoggerMapper loggerMapper;


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @TestConfiguration
    static class AuthenticationPrincipalResolverConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }

    @DisplayName("회원가입 성공 시  유저 요약 정보를 반환한다")
    @Test
    void register_success() throws Exception {
        // given
        UserSummaryResponse summary = UserSummaryResponse.builder()
                .userId(1L)
                .email("codex@example.com")
                .name("codex")
                .role("ROLE_USER")
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("codex@example.com"))
                .andExpect(jsonPath("$.name").value("codex"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

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

    @DisplayName("/api/user/me - 로그인된 사용자의 요약 정보를 반환한다")
    @Test
    void me_success() throws Exception {
        // given
        UserSummaryResponse summary = UserSummaryResponse.builder()
                .userId(1L)
                .email("me@example.com")
                .name("me")
                .role("ROLE_USER")
                .build();
        given(userService.findSummary(1L)).willReturn(summary);

        JwtPrincipal principal = new JwtPrincipal(
                1L,
                "me@example.com",
                "me",
                "ROLE_USER",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true,  // accountNonExpired
                true,  // accountNonLocked
                true,  // credentialsNonExpired
                true   // enabled
        );
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // when & then
        mockMvc.perform(get("/api/user/me")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("me@example.com"))
                .andExpect(jsonPath("$.name").value("me"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(userService).findSummary(1L);
    }


    @DisplayName("/api/user/logout - 쿠키를 삭제하고 보안 컨텍스트를 비운다")
    @Test
    void logout_clearsCookiesAndContext() throws Exception {
        JwtPrincipal principal = new JwtPrincipal(
                1L,
                "logout@example.com",
                "logout",
                "ROLE_USER",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true,
                true,
                true,
                true
        );
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        MvcResult result = mockMvc.perform(post("/api/user/logout")
                .with(authentication(authenticationToken)))
                .andExpect(status().isNoContent())
                .andReturn();

        List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(3);
        assertThat(setCookies).anyMatch(c -> c.startsWith("ACCESS_TOKEN=") && c.contains("Max-Age=0"));
        assertThat(setCookies).anyMatch(c -> c.startsWith("ACCESS_ISSUED_AT=") && c.contains("Max-Age=0"));
        assertThat(setCookies).anyMatch(c -> c.startsWith("ACCESS_EXPIRES_AT=") && c.contains("Max-Age=0"));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();


    }
}
