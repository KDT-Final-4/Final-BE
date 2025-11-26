package com.final_team4.finalbe.logger.dto;

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
public class PipelineLogCreateRequest {
  private Long userId;
  private LogType logType;
  private String loggedProcess;
  private LocalDateTime loggedDate;
  private String message;
  private String submessage;
  private String jobId;
}
