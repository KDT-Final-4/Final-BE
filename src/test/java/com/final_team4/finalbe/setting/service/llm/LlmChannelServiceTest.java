package com.final_team4.finalbe.setting.service.llm;

import com.final_team4.finalbe._core.exception.BadRequestException;
import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelCreateRequestDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelUpdateRequestDto;
import com.final_team4.finalbe.setting.mapper.llm.LlmChannelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LlmChannelServiceTest {

  @Autowired
  private LlmChannelService llmChannelService;

  @Autowired
  private LlmChannelMapper llmChannelMapper;

  private static final Long TEST_USER_ID = 3L;

  @DisplayName("성공_사용자별 LLM 설정 조회")
  @Test
  void findByUserId_success() {
    // given
    LlmChannel entity = createTestLlmChannel(TEST_USER_ID);
    llmChannelMapper.insert(entity);

    // when
    LlmChannelDetailResponseDto result = llmChannelService.findByUserId(TEST_USER_ID);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
    assertThat(result.getName()).isEqualTo("Test LLM Channel");
    assertThat(result.getModelName()).isEqualTo("gpt-4");
    assertThat(result.getMaxTokens()).isEqualTo(2000);
    assertThat(result.getTemperature()).isEqualByComparingTo(new BigDecimal("0.7"));
  }

  @DisplayName("실패_LLM 설정이 없으면 ContentNotFoundException 발생")
  @Test
  void findByUserId_notFound() {
    // given
    Long nonExistentUserId = 999L;

    // when & then
    assertThatThrownBy(() -> llmChannelService.findByUserId(nonExistentUserId))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("LLM 설정을 찾을 수 없습니다.");
  }

  @DisplayName("성공_LLM 설정 수정")
  @Test
  void update_success() {
    // given
    LlmChannel entity = createTestLlmChannel(TEST_USER_ID);
    llmChannelMapper.insert(entity);

    LlmChannelUpdateRequestDto updateRequest = LlmChannelUpdateRequestDto.builder()
        .name("Updated LLM Channel")
        .modelName("gpt-4-turbo")
        .apiKey("sk-updated-key-1234")
        .status(true)
        .maxTokens(3000)
        .temperature(new BigDecimal("0.85"))
        .prompt("Updated prompt")
        .generationType(ContentGenType.MANUAL)
        .build();

    // when
    LlmChannelDetailResponseDto result = llmChannelService.update(TEST_USER_ID, updateRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Updated LLM Channel");
    assertThat(result.getModelName()).isEqualTo("gpt-4-turbo");
    assertThat(result.getMaxTokens()).isEqualTo(3000);
    assertThat(result.getTemperature()).isEqualByComparingTo(new BigDecimal("0.85"));
    assertThat(result.getStatus()).isTrue();
    assertThat(result.getPrompt()).isEqualTo("Updated prompt");
    assertThat(result.getGenerationType()).isEqualTo(ContentGenType.MANUAL);
  }

  @DisplayName("성공_LLM 설정 부분 수정 (일부 필드만 업데이트)")
  @Test
  void update_partial_success() {
    // given
    LlmChannel entity = createTestLlmChannel(TEST_USER_ID);
    llmChannelMapper.insert(entity);

    LlmChannelUpdateRequestDto updateRequest = LlmChannelUpdateRequestDto.builder()
        .name("Partially Updated")
        .modelName("gpt-4")
        .maxTokens(2500)
        .generationType(ContentGenType.AUTO)
        .build();

    // when
    LlmChannelDetailResponseDto result = llmChannelService.update(TEST_USER_ID, updateRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Partially Updated");
    assertThat(result.getModelName()).isEqualTo("gpt-4");
    assertThat(result.getMaxTokens()).isEqualTo(2500);
    assertThat(result.getGenerationType()).isEqualTo(ContentGenType.AUTO);
    // 기존 값 유지 확인
    assertThat(result.getTemperature()).isEqualByComparingTo(new BigDecimal("0.7"));
  }

  @DisplayName("실패_수정할 LLM 설정이 없으면 ContentNotFoundException 발생")
  @Test
  void update_notFound() {
    // given
    Long nonExistentUserId = 999L;
    LlmChannelUpdateRequestDto updateRequest = LlmChannelUpdateRequestDto.builder()
        .modelName("gpt-4")
        .generationType(ContentGenType.AUTO)
        .build();

    // when & then
    assertThatThrownBy(() -> llmChannelService.update(nonExistentUserId, updateRequest))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("LLM 설정을 찾을 수 없습니다.");
  }

  @DisplayName("성공_ID로 LLM 설정 조회")
  @Test
  void findById_success() {
    // given
    LlmChannel entity = createTestLlmChannel(TEST_USER_ID);
    llmChannelMapper.insert(entity);
    Long channelId = entity.getId();

    // when
    LlmChannelDetailResponseDto result = llmChannelService.findById(TEST_USER_ID, channelId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(channelId);
    assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
    assertThat(result.getName()).isEqualTo("Test LLM Channel");
  }

  @DisplayName("실패_ID로 조회 시 LLM 설정이 없으면 ContentNotFoundException 발생")
  @Test
  void findById_notFound() {
    // given
    Long nonExistentId = 999L;

    // when & then
    assertThatThrownBy(() -> llmChannelService.findById(TEST_USER_ID, nonExistentId))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("LLM 설정을 찾을 수 없습니다.");
  }

  @DisplayName("성공_LLM 설정 등록")
  @Test
  void create_success() {
    // given
    LlmChannelCreateRequestDto createRequest = LlmChannelCreateRequestDto.builder()
        .name("New LLM Channel")
        .modelName("gpt-4")
        .apiKey("sk-new-key-12345678")
        .status(true)
        .maxTokens(2000)
        .temperature(new BigDecimal("0.7"))
        .prompt("You are a helpful assistant.")
        .generationType(ContentGenType.AUTO)
        .build();

    // when
    LlmChannelDetailResponseDto result = llmChannelService.create(TEST_USER_ID, createRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
    assertThat(result.getName()).isEqualTo("New LLM Channel");
    assertThat(result.getModelName()).isEqualTo("gpt-4");
    assertThat(result.getApiKey()).isNotNull(); // 마스킹 처리됨
    assertThat(result.getStatus()).isTrue();
    assertThat(result.getMaxTokens()).isEqualTo(2000);
    assertThat(result.getTemperature()).isEqualByComparingTo(new BigDecimal("0.7"));
    assertThat(result.getPrompt()).isEqualTo("You are a helpful assistant.");
    assertThat(result.getGenerationType()).isEqualTo(ContentGenType.AUTO);

    // DB에 실제로 저장되었는지 확인
    LlmChannel saved = llmChannelMapper.findByUserId(TEST_USER_ID);
    assertThat(saved).isNotNull();
    assertThat(saved.getName()).isEqualTo("New LLM Channel");
    assertThat(saved.getPrompt()).isEqualTo("You are a helpful assistant.");
    assertThat(saved.getGenerationType()).isEqualTo(ContentGenType.AUTO);
  }

  @DisplayName("성공_등록 시 기본값 처리 확인")
  @Test
  void create_withDefaultValues() {
    // given - 최소 필드만 입력
    LlmChannelCreateRequestDto createRequest = LlmChannelCreateRequestDto.builder()
        .name("Minimal LLM Channel")
        .modelName("gpt-4")
        .apiKey("sk-minimal-key-12345678")
        .generationType(ContentGenType.AUTO)
        // maxTokens, temperature, status는 null
        .build();

    // when
    LlmChannelDetailResponseDto result = llmChannelService.create(TEST_USER_ID, createRequest);

    // then - 기본값 확인
    assertThat(result).isNotNull();
    assertThat(result.getMaxTokens()).isEqualTo(2000); // 기본값
    assertThat(result.getTemperature()).isEqualByComparingTo(new BigDecimal("0.7")); // 기본값
    assertThat(result.getStatus()).isFalse(); // 기본값: false
    assertThat(result.getGenerationType()).isEqualTo(ContentGenType.AUTO);
  }

  @DisplayName("실패_이미 LLM 설정이 존재하면 BadRequestException 발생")
  @Test
  void create_alreadyExists() {
    // given
    LlmChannel existing = createTestLlmChannel(TEST_USER_ID);
    llmChannelMapper.insert(existing);

    LlmChannelCreateRequestDto createRequest = LlmChannelCreateRequestDto.builder()
        .name("Duplicate Channel")
        .modelName("gpt-4")
        .apiKey("sk-duplicate-key-12345678")
        .generationType(ContentGenType.AUTO)
        .build();

    // when & then
    assertThatThrownBy(() -> llmChannelService.create(TEST_USER_ID, createRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("이미 LLM 설정이 존재합니다. 수정 API를 사용해주세요.");
  }

  @DisplayName("실패_API 키가 없으면 BadRequestException 발생")
  @Test
  void create_apiKeyRequired() {
    // given
    LlmChannelCreateRequestDto createRequest = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        .modelName("gpt-4")
        .generationType(ContentGenType.AUTO)
        // apiKey가 null
        .build();

    // when & then
    assertThatThrownBy(() -> llmChannelService.create(TEST_USER_ID, createRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("API 키는 필수입니다.");
  }

  @DisplayName("실패_API 키가 빈 문자열이면 BadRequestException 발생")
  @Test
  void create_apiKeyEmpty() {
    // given
    LlmChannelCreateRequestDto createRequest = LlmChannelCreateRequestDto.builder()
        .name("Test LLM")
        .modelName("gpt-4")
        .apiKey("   ") // 공백만 있는 경우
        .generationType(ContentGenType.AUTO)
        .build();

    // when & then
    assertThatThrownBy(() -> llmChannelService.create(TEST_USER_ID, createRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("API 키는 필수입니다.");
  }

  private LlmChannel createTestLlmChannel(Long userId) {
    return LlmChannel.builder()
        .userId(userId)
        .name("Test LLM Channel")
        .modelName("gpt-4")
        .apiKey("sk-test-key-12345678")
        .status(false)
        .maxTokens(2000)
        .temperature(new BigDecimal("0.7"))
        .prompt("Test prompt")
        .generationType(ContentGenType.AUTO)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}

