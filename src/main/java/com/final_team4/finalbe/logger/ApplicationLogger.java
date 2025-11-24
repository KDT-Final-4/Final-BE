package com.final_team4.finalbe.logger;

import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.service.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationLogger {

  public static final long SYSTEM_USER_ID = 1L;
  private static final long DEFAULT_JOB_ID = 0L;

  private final LoggerService loggerService;

  public void log(String message) {
    log(message, LogType.INFO);
  }

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
