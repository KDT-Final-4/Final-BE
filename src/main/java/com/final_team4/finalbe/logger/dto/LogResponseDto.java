package com.final_team4.finalbe.logger.dto;

import com.final_team4.finalbe.logger.domain.Log;
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
  private Long id;
  private Long userId;
  private Long typeId;
  private Long jobId;
  private String message;
  private LocalDateTime createdAt;

  public static LogResponseDto from(Log log) {
    return LogResponseDto.builder()
        .id(log.getId())
        .userId(log.getUserId())
        .typeId(log.getTypeId())
        .jobId(log.getJobId())
        .message(log.getMessage())
        .createdAt(log.getCreatedAt())
        .build();
  }
}
