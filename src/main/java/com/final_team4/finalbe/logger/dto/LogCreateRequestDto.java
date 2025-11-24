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
  /**
   * 로그 생성 요청 DTO. 서비스/애플리케이션 로거에서 사용합니다.
   */
  @NotNull
  private Long userId;
  private LogType logType;
  private Long jobId;
  @NotNull
  private String message;

  /**
   * 기본값을 채워 엔티티로 변환합니다. 외부에 노출되지 않고 서비스 내부에서만 사용됩니다.
   */
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
