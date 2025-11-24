package com.final_team4.finalbe.logger.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.domain.type.LogType;
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

  /**
   * 기본 로그 타입과 jobId는 별도 지정이 없을 때 사용됩니다.
   */
  private static final LogType DEFAULT_LOG_TYPE = LogType.INFO;
  private static final long DEFAULT_JOB_ID = 0L;

  private final LoggerMapper loggerMapper;

  /**
   * 로그를 생성하고 콘솔에 출력합니다. 외부에서 직접 호출됩니다.
   */
  @Transactional
  public LogResponseDto createLog(LogCreateRequestDto requestDto) {
    Log log = requestDto.toEntity(DEFAULT_LOG_TYPE, DEFAULT_JOB_ID);
    loggerMapper.insert(log);
    printLog(log);
    return LogResponseDto.from(log);
  }

  /**
   * ID로 로그를 조회합니다. 존재하지 않으면 예외를 던집니다.
   */
  public LogResponseDto findById(Long id) {
    Log log = loggerMapper.findById(id);
    if (log == null) {
      throw new ContentNotFoundException("해당 로그를 찾을 수 없습니다.");
    }
    return LogResponseDto.from(log);
  }

  /**
   * userId, logType 조건으로 최신 30개를 오래된 순으로 반환합니다.
   */
  public List<LogResponseDto> findRecentLogs(Long userId, LogType logType) {
    List<Log> logs = loggerMapper.findRecentLogs(userId, logType.getId(), 30);
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  /**
   * jobId로 모든 로그를 조회합니다. 결과가 없으면 예외를 던집니다.
   */
  public List<LogResponseDto> findByJobId(Long jobId) {
    List<Log> logs = loggerMapper.findByJobId(jobId);
    if (logs.isEmpty()) {
      throw new ContentNotFoundException("해당 JOB ID의 로그를 찾을 수 없습니다.");
    }
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  /**
   * DB에 저장된 로그를 콘솔에 포맷팅해 출력합니다. 외부에서 호출할 필요 없습니다.
   */
  private void printLog(Log log) {
    String typeName = resolveTypeName(log.getLogType());
    String caller = resolveCaller(log.getUserId());
    String message = log.getMessage();
    System.out.println("[" + typeName + "] \"" + caller + "\":  " + message);
  }

  /**
   * 로그 타입을 문자열 라벨로 변환합니다.
   */
  private String resolveTypeName(LogType logType) {
    if (logType == null) {
      return "UNKNOWN";
    }
    return logType.getLabel();
  }

  /**
   * 호출자를 출력용 문자열로 변환합니다. 외부 호출 필요 없습니다.
   */
  private String resolveCaller(Long userId) {
    if (userId != null && userId == 1L) {
      return "SYSTEM";
    }
    return "user_id: " + userId;
  }
}
