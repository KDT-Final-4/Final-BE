package com.final_team4.finalbe.logger.dto;

import com.final_team4.finalbe.logger.domain.Log;
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
  private Long typeId;
  private Long jobId;
  @NotNull
  private String message;

  public Log toEntity(Long defaultTypeId, Long defaultJobId) {
    Long resolvedTypeId = typeId != null ? typeId : defaultTypeId;
    Long resolvedJobId = jobId != null ? jobId : defaultJobId;
    return Log.builder()
        .userId(userId)
        .typeId(resolvedTypeId)
        .jobId(resolvedJobId)
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
