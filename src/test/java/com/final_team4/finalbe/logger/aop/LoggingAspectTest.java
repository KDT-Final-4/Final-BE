package com.final_team4.finalbe.logger.aop;

import com.final_team4.finalbe.logger.ApplicationLogger;
import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.service.LoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

  @Mock
  private LoggerService loggerService;

  private LoggingAspect loggingAspect;

  @BeforeEach
  void setUp() {
    ApplicationLogger applicationLogger = new ApplicationLogger(loggerService);
    loggingAspect = new LoggingAspect(applicationLogger);
  }

  // @Loggable 메서드 실행 시 전달한 메시지로 시스템 유저/INFO 타입 로그를 남기는지 검증
  @DisplayName("@Loggable 메서드 실행 시 지정한 메시지로 로그가 기록된다")
  @Test
  void loggableMethod_logsMessage() {
    // given
    ArgumentCaptor<LogCreateRequestDto> captor = ArgumentCaptor.forClass(LogCreateRequestDto.class);
    Sample target = new Sample();
    AspectJProxyFactory factory = new AspectJProxyFactory(target);
    factory.addAspect(loggingAspect);
    Sample proxy = factory.getProxy();

    // when
    proxy.runTask();

    // then
    verify(loggerService).createLog(captor.capture());
    LogCreateRequestDto sent = captor.getValue();
    assertThat(sent.getMessage()).isEqualTo("sample task executed");
    assertThat(sent.getUserId()).isEqualTo(ApplicationLogger.SYSTEM_USER_ID);
    assertThat(sent.getLogType()).isEqualTo(LogType.INFO);
  }

  private static class Sample {
    public Sample() {
    }

    @Loggable("sample task executed")
    public void runTask() {
      // no-op
    }
  }
}
