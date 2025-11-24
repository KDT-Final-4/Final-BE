package com.final_team4.finalbe.logger.dto;

import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.domain.type.LogType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LogCreateRequestDto {
  @NotNull
  private Long userId;
  private LogType logType;
  private Long jobId;
  @NotNull
  private String message;

  public Log toEntity(LogType defaultLogType, Long defaultJobId) {
    LogType resolvedLogType = logType != null ? logType : defaultLogType;
    Long resolvedJobId = jobId != null ? jobId : defaultJobId;
    return Log.builder()
        .userId(userId)
        .logType(resolvedLogType)
        .jobId(resolvedJobId)
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
