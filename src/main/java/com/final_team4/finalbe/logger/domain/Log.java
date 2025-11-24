package com.final_team4.finalbe.logger.domain;

import com.final_team4.finalbe.logger.domain.type.LogType;
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
  /**
   * 로그 엔티티. DB LOG 테이블과 매핑됩니다.
   */
  private Long id;
  private Long userId;
  private LogType logType;
  private Long jobId;
  private String message;
  private LocalDateTime createdAt;
}
