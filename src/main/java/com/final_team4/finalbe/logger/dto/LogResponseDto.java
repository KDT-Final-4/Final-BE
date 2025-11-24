package com.final_team4.finalbe.logger.dto;

import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.domain.type.LogType;
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
public class LogResponseDto {
  /**
   * 로그 응답 DTO. 서비스 호출 결과를 외부로 반환할 때 사용합니다.
   */
  private Long id;
  private Long userId;
  private LogType logType;
  private Long jobId;
  private String message;
  private LocalDateTime createdAt;

  /**
   * 엔티티를 응답 DTO로 변환합니다. 서비스 내부에서만 사용됩니다.
   */
  public static LogResponseDto from(Log log) {
    return LogResponseDto.builder()
        .id(log.getId())
        .userId(log.getUserId())
        .logType(log.getLogType())
        .jobId(log.getJobId())
        .message(log.getMessage())
        .createdAt(log.getCreatedAt())
        .build();
  }
}
