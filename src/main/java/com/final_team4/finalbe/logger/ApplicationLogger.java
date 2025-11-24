package com.final_team4.finalbe.logger;

import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.service.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationLogger {

  /**
   * 환경 설정이 없을 때 사용할 기본 시스템 유저 ID.
   */
  public static final long DEFAULT_SYSTEM_USER_ID = 1L;
  /**
   * 별도 지정이 없을 때 사용할 기본 jobId.
   */
  private static final long DEFAULT_JOB_ID = 0L;

  private final LoggerService loggerService;
  /**
   * 설정에서 주입되는 시스템 유저 ID. 없으면 기본값(1) 사용.
   */
  private final long systemUserId;

  public ApplicationLogger(LoggerService loggerService,
                           @Value("${logging.system-user-id:" + DEFAULT_SYSTEM_USER_ID + "}") long systemUserId) {
    this.loggerService = loggerService;
    this.systemUserId = systemUserId;
  }

  public long getSystemUserId() {
    return systemUserId;
  }

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
        .userId(systemUserId)
        .logType(logType)
        .jobId(DEFAULT_JOB_ID)
        .message(message)
        .build();
    loggerService.createLog(requestDto);
  }
}
