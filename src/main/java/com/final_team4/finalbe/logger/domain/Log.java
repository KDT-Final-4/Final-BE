package com.final_team4.finalbe.logger.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Log {
  private Long id;
  private Long userId;
  private Long typeId;
  private Long jobId;
  private String message;
  private LocalDateTime createdAt;
}
