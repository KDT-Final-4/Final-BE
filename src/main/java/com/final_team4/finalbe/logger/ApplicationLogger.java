package com.final_team4.finalbe.logger;

import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.service.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationLogger {

  /**
   * 시스템 호출자로 사용할 기본 유저 ID (직접 호출 시 세션 정보 없이 남길 때 사용).
   */
  public static final long SYSTEM_USER_ID = 1L;
  /**
   * 별도 지정이 없을 때 사용할 기본 jobId.
   */
  private static final long DEFAULT_JOB_ID = 0L;

  private final LoggerService loggerService;

  /**
   * INFO 레벨로 단순 메시지를 로그에 남깁니다. 앱 어디서나 호출 가능합니다.
   */
  public void log(String message) {
    log(message, LogType.INFO);
  }

  /**
   * 원하는 로그 타입으로 메시지를 남깁니다. 비즈니스 코드에서 직접 호출해 사용합니다.
   */
  public void log(String message, LogType logType) {
    LogCreateRequestDto requestDto = LogCreateRequestDto.builder()
        .userId(SYSTEM_USER_ID)
        .logType(logType)
        .jobId(DEFAULT_JOB_ID)
        .message(message)
        .build();
    loggerService.createLog(requestDto);
  }
}
