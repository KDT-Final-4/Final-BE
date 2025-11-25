package com.final_team4.finalbe.logger;

import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.dto.LogResponseDto;
import com.final_team4.finalbe.logger.service.LoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationLoggerTest {

  @Mock
  private LoggerService loggerService;

  private ApplicationLogger applicationLogger;

  @BeforeEach
  void setUp() {
    applicationLogger = ApplicationLogger.builder()
        .loggerService(loggerService)
        .systemUserId(ApplicationLogger.DEFAULT_SYSTEM_USER_ID)
        .build();
  }

  // logger.log() 호출 시 시스템 유저, INFO 타입, 기본 jobId로 로그 생성이 위임되는지 검증
  @DisplayName("logger.log 호출 시 시스템 유저, INFO 타입으로 로그가 생성된다")
  @Test
  void log_withMessage_usesSystemUserAndInfoType() {
    // given
    ArgumentCaptor<LogCreateRequestDto> captor = ArgumentCaptor.forClass(LogCreateRequestDto.class);
    given(loggerService.createLog(any(LogCreateRequestDto.class)))
        .willAnswer(invocation -> {
          LogCreateRequestDto req = invocation.getArgument(0);
          return LogResponseDto.builder()
              .id(10L)
              .userId(req.getUserId())
              .logType(req.getLogType())
              .jobId(req.getJobId())
              .message(req.getMessage())
              .build();
        });

    // when
    applicationLogger.log("hello logger");

    // then
    verify(loggerService).createLog(captor.capture());
    LogCreateRequestDto sent = captor.getValue();
    assertThat(sent.getUserId()).isEqualTo(applicationLogger.getSystemUserId());
    assertThat(sent.getLogType()).isEqualTo(LogType.INFO);
    assertThat(sent.getJobId()).isEqualTo(0L);
    assertThat(sent.getMessage()).isEqualTo("hello logger");
  }

  // logger.log(message, type) 호출 시 지정한 로그 타입으로 기록되는지 검증
  @DisplayName("logger.log(message, typeId) 호출 시 지정한 타입으로 로그가 생성된다")
  @Test
  void log_withMessageAndType_usesProvidedType() {
    // given
    ArgumentCaptor<LogCreateRequestDto> captor = ArgumentCaptor.forClass(LogCreateRequestDto.class);
    given(loggerService.createLog(any(LogCreateRequestDto.class)))
        .willAnswer(invocation -> {
          LogCreateRequestDto req = invocation.getArgument(0);
          return LogResponseDto.builder()
              .id(11L)
              .userId(req.getUserId())
              .logType(req.getLogType())
              .jobId(req.getJobId())
              .message(req.getMessage())
              .build();
        });

    // when
    applicationLogger.log("custom type log", LogType.ERROR);

    // then
    verify(loggerService).createLog(captor.capture());
    LogCreateRequestDto sent = captor.getValue();
    assertThat(sent.getUserId()).isEqualTo(applicationLogger.getSystemUserId());
    assertThat(sent.getLogType()).isEqualTo(LogType.ERROR);
    assertThat(sent.getJobId()).isEqualTo(0L);
    assertThat(sent.getMessage()).isEqualTo("custom type log");
  }
}
