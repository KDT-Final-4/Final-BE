package com.final_team4.finalbe.setting.controller.llm;

import com.final_team4.finalbe._core.config.GlobalExceptionHandler;
import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe._core.exception.BadRequestException;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelCreateRequestDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelUpdateRequestDto;
import com.final_team4.finalbe.setting.service.llm.LlmChannelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = LlmChannelController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        MybatisAutoConfiguration.class
    }
)
@Import(GlobalExceptionHandler.class)
class LlmChannelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  LlmChannelService llmChannelService;

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

  private JwtPrincipal createJwtPrincipal(Long userId) {
    return JwtPrincipal.builder()
        .userId(userId)
        .email("test" + userId + "@example.com")
        .name("testUser" + userId)
        .role("ROLE_USER")
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .enabled(true)
        .build();
  }

  @DisplayName("성공_LLM 설정 조회 시 200 OK와 상세 정보 반환")
  @Test
  void getLlmSetting_success() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);
    
    LlmChannelDetailResponseDto responseDto = LlmChannelDetailResponseDto.builder()
        .id(1L)
        .userId(userId)
        .name("Test LLM Channel")
        .modelName("gpt-4")
        .baseUrl("https://api.openai.com/v1")
        .status(true)
        .maxTokens(2000)
        .temperature(new BigDecimal("0.7"))
        .topP(new BigDecimal("0.8"))
        .apiKey("****************1234")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(llmChannelService.findByUserId(userId))
        .willReturn(responseDto);

    // when & then
    mockMvc.perform(get("/api/setting/llm")
            .with(authentication(createAuthToken(principal))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.name").value("Test LLM Channel"))
        .andExpect(jsonPath("$.modelName").value("gpt-4"))
        .andExpect(jsonPath("$.maxTokens").value(2000))
        .andExpect(jsonPath("$.temperature").value(0.7))
        .andExpect(jsonPath("$.status").value(true));

    verify(llmChannelService).findByUserId(userId);
  }

  @DisplayName("실패_인증된 사용자 정보가 없으면 500 에러 발생")
  @Test
  void getLlmSetting_unauthorized() throws Exception {
    // when & then
    mockMvc.perform(get("/api/setting/llm"))
        .andExpect(status().is5xxServerError());
  }

  @DisplayName("실패_LLM 설정이 없으면 404 에러 발생")
  @Test
  void getLlmSetting_notFound() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    given(llmChannelService.findByUserId(userId))
        .willThrow(new ContentNotFoundException("LLM 설정을 찾을 수 없습니다."));

    // when & then
    mockMvc.perform(get("/api/setting/llm")
            .with(authentication(createAuthToken(principal))))
        .andExpect(status().isNotFound());
  }

  @DisplayName("성공_LLM 설정 등록 시 201 Created와 상세 정보 반환")
  @Test
  void createLlmSetting_success() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        .name("New LLM Channel")
        .modelName("gpt-4")
        .apiKey("sk-new-key-12345678")
        .baseUrl("https://api.openai.com/v1")
        .status(true)
        .maxTokens(2000)
        .temperature(new BigDecimal("0.7"))
        .topP(new BigDecimal("0.9"))
        .prompt("You are a helpful assistant.")
        .build();

    LlmChannelDetailResponseDto responseDto = LlmChannelDetailResponseDto.builder()
        .id(1L)
        .userId(userId)
        .name("New LLM Channel")
        .modelName("gpt-4")
        .baseUrl("https://api.openai.com/v1")
        .status(true)
        .maxTokens(2000)
        .temperature(new BigDecimal("0.7"))
        .topP(new BigDecimal("0.9"))
        .prompt("You are a helpful assistant.")
        .apiKey("****************5678")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(llmChannelService.create(eq(userId), any(LlmChannelCreateRequestDto.class)))
        .willReturn(responseDto);

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.name").value("New LLM Channel"))
        .andExpect(jsonPath("$.modelName").value("gpt-4"))
        .andExpect(jsonPath("$.maxTokens").value(2000))
        .andExpect(jsonPath("$.temperature").value(0.7))
        .andExpect(jsonPath("$.topP").value(0.9))
        .andExpect(jsonPath("$.prompt").value("You are a helpful assistant."))
        .andExpect(jsonPath("$.status").value(true));

    verify(llmChannelService).create(eq(userId), any(LlmChannelCreateRequestDto.class));
  }

  @DisplayName("실패_등록 시 인증된 사용자 정보가 없으면 500 에러 발생")
  @Test
  void createLlmSetting_unauthorized() throws Exception {
    // given
    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        .modelName("gpt-4")
        .apiKey("sk-test-key")
        .build();

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().is5xxServerError());
  }

  @DisplayName("실패_등록 시 필수 필드(name)가 없으면 400 에러 발생")
  @Test
  void createLlmSetting_nameRequired() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        // name이 null (필수 필드)
        .modelName("gpt-4")
        .apiKey("sk-test-key")
        .build();

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("실패_등록 시 필수 필드(modelName)가 없으면 400 에러 발생")
  @Test
  void createLlmSetting_modelNameRequired() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        // modelName이 null (필수 필드)
        .apiKey("sk-test-key")
        .build();

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("실패_등록 시 API 키가 없으면 400 에러 발생")
  @Test
  void createLlmSetting_apiKeyRequired() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        .modelName("gpt-4")
        // apiKey가 null (비즈니스 로직 검증)
        .build();

    given(llmChannelService.create(eq(userId), any(LlmChannelCreateRequestDto.class)))
        .willThrow(new BadRequestException("API 키는 필수입니다."));

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("API 키는 필수입니다."));

    verify(llmChannelService).create(eq(userId), any(LlmChannelCreateRequestDto.class));
  }

  @DisplayName("실패_이미 LLM 설정이 존재하면 400 에러 발생")
  @Test
  void createLlmSetting_alreadyExists() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelCreateRequestDto requestDto = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        .modelName("gpt-4")
        .apiKey("sk-test-key-12345678")
        .build();

    given(llmChannelService.create(eq(userId), any(LlmChannelCreateRequestDto.class)))
        .willThrow(new BadRequestException("이미 LLM 설정이 존재합니다. 수정 API를 사용해주세요."));

    // when & then
    mockMvc.perform(post("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("이미 LLM 설정이 존재합니다. 수정 API를 사용해주세요."));

    verify(llmChannelService).create(eq(userId), any(LlmChannelCreateRequestDto.class));
  }

  @DisplayName("성공_LLM 설정 수정 시 200 OK와 수정된 정보 반환")
  @Test
  void updateLlmSetting_success() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelUpdateRequestDto requestDto = LlmChannelUpdateRequestDto.builder()
        .name("Updated LLM Channel")
        .modelName("gpt-4-turbo")
        .apiKey("sk-updated-key-1234")
        .baseUrl("https://api.openai.com/v1")
        .status(true)
        .maxTokens(3000)
        .temperature(new BigDecimal("0.85"))
        .topP(new BigDecimal("0.90"))
        .prompt("Updated prompt")
        .build();

    LlmChannelDetailResponseDto responseDto = LlmChannelDetailResponseDto.builder()
        .id(1L)
        .userId(userId)
        .name("Updated LLM Channel")
        .modelName("gpt-4-turbo")
        .baseUrl("https://api.openai.com/v1")
        .status(true)
        .maxTokens(3000)
        .temperature(new BigDecimal("0.85"))
        .topP(new BigDecimal("0.90"))
        .prompt("Updated prompt")
        .apiKey("****************1234")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(llmChannelService.update(eq(userId), any(LlmChannelUpdateRequestDto.class)))
        .willReturn(responseDto);

    // when & then
    mockMvc.perform(put("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.name").value("Updated LLM Channel"))
        .andExpect(jsonPath("$.modelName").value("gpt-4-turbo"))
        .andExpect(jsonPath("$.maxTokens").value(3000))
        .andExpect(jsonPath("$.temperature").value(0.85))
        .andExpect(jsonPath("$.topP").value(0.90))
        .andExpect(jsonPath("$.prompt").value("Updated prompt"))
        .andExpect(jsonPath("$.status").value(true));

    verify(llmChannelService).update(eq(userId), any(LlmChannelUpdateRequestDto.class));
  }

  @DisplayName("실패_수정 시 인증된 사용자 정보가 없으면 500 에러 발생")
  @Test
  void updateLlmSetting_unauthorized() throws Exception {
    // given
    LlmChannelUpdateRequestDto requestDto = LlmChannelUpdateRequestDto.builder()
        .modelName("gpt-4")
        .build();

    // when & then
    mockMvc.perform(put("/api/setting/llm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().is5xxServerError());
  }

  @DisplayName("실패_수정 시 필수 필드(modelName)가 없으면 400 에러 발생")
  @Test
  void updateLlmSetting_validationFailed() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelUpdateRequestDto requestDto = LlmChannelUpdateRequestDto.builder()
        .name("Test")
        // modelName이 null (필수 필드)
        .build();

    // when & then
    mockMvc.perform(put("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("실패_수정 시 LLM 설정이 없으면 404 에러 발생")
  @Test
  void updateLlmSetting_notFound() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    LlmChannelUpdateRequestDto requestDto = LlmChannelUpdateRequestDto.builder()
        .modelName("gpt-4")
        .build();

    given(llmChannelService.update(eq(userId), any(LlmChannelUpdateRequestDto.class)))
        .willThrow(new ContentNotFoundException("LLM 설정을 찾을 수 없습니다."));

    // when & then
    mockMvc.perform(put("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());
  }

  @DisplayName("실패_수정 시 temperature가 범위를 벗어나면 400 에러 발생")
  @Test
  void updateLlmSetting_temperatureOutOfRange() throws Exception {
    // given
    Long userId = 1L;
    JwtPrincipal principal = createJwtPrincipal(userId);

    String requestBody = """
        {
          "modelName": "gpt-4",
          "temperature": 3.0
        }
        """;

    // when & then
    mockMvc.perform(put("/api/setting/llm")
            .with(authentication(createAuthToken(principal)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  private org.springframework.security.authentication.UsernamePasswordAuthenticationToken createAuthToken(
      JwtPrincipal principal) {
    return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
        principal, "token", principal.getAuthorities());
  }
}

