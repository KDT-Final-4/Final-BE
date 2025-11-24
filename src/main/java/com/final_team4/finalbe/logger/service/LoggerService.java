package com.final_team4.finalbe.logger.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.dto.LogResponseDto;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoggerService {

  private static final long DEFAULT_TYPE_ID = 1L;
  private static final long DEFAULT_JOB_ID = 0L;

  private final LoggerMapper loggerMapper;

  @Transactional
  public LogResponseDto createLog(LogCreateRequestDto requestDto) {
    Log log = requestDto.toEntity(DEFAULT_TYPE_ID, DEFAULT_JOB_ID);
    loggerMapper.insert(log);
    printLog(log);
    return LogResponseDto.from(log);
  }

  public LogResponseDto findById(Long id) {
    Log log = loggerMapper.findById(id);
    if (log == null) {
      throw new ContentNotFoundException("해당 로그를 찾을 수 없습니다.");
    }
    return LogResponseDto.from(log);
  }

  public List<LogResponseDto> findRecentLogs(Long userId, Long typeId) {
    List<Log> logs = loggerMapper.findRecentLogs(userId, typeId, 30);
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  public List<LogResponseDto> findByJobId(Long jobId) {
    List<Log> logs = loggerMapper.findByJobId(jobId);
    if (logs.isEmpty()) {
      throw new ContentNotFoundException("해당 JOB ID의 로그를 찾을 수 없습니다.");
    }
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  private void printLog(Log log) {
    String typeName = resolveTypeName(log.getTypeId());
    String caller = resolveCaller(log.getUserId());
    String message = log.getMessage();
    System.out.println("[" + typeName + "] \"" + caller + "\":  " + message);
  }

  private String resolveTypeName(Long typeId) {
    if (typeId == null) {
      return "UNKNOWN";
    }
    if (typeId == 1L) {
      return "INFO";
    }
    if (typeId == 2L) {
      return "ERROR";
    }
    return "UNKNOWN";
  }

  private String resolveCaller(Long userId) {
    if (userId != null && userId == 1L) {
      return "SYSTEM";
    }
    return "user_id: " + userId;
  }
}
