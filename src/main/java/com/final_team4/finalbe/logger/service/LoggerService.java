package com.final_team4.finalbe.logger.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.dto.LogTypeCountRow;
import com.final_team4.finalbe.logger.dto.LogResponseDto;
import com.final_team4.finalbe.logger.dto.PipelineLogCreateRequest;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoggerService {

  /**
   * 기본 로그 타입과 jobId는 별도 지정이 없을 때 사용됩니다.
   */
  private static final LogType DEFAULT_LOG_TYPE = LogType.INFO;
  private static final String DEFAULT_JOB_ID = "0";
  private static final long SSE_TIMEOUT_MS = 60_000L;

  private final LoggerMapper loggerMapper;

  @Qualifier("virtualThreadTaskExecutor")
  private final TaskExecutor taskExecutor;

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
    Long typeId = logType != null ? logType.getId() : null;
    List<Log> logs = loggerMapper.findRecentLogs(userId, typeId, 30);
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  /**
   * jobId로 모든 로그를 조회합니다. 결과가 없으면 예외를 던집니다.
   */
  public List<LogResponseDto> findByJobId(String jobId) {
    List<Log> logs = loggerMapper.findByJobId(jobId);
    if (logs.isEmpty()) {
      throw new ContentNotFoundException("해당 JOB ID의 로그를 찾을 수 없습니다.");
    }
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  /**
   * 파이프라인 로그 생성용 헬퍼. 전달된 필드로 메시지를 조립해 저장합니다.
   */
  @Transactional
  public LogResponseDto createPipelineLog(PipelineLogCreateRequest request) {
    String formattedMessage = formatPipelineMessage(request);
    LogCreateRequestDto createRequest = LogCreateRequestDto.builder()
        .userId(request.getUserId())
        .logType(request.getLogType())
        .jobId(request.getJobId())
        .message(formattedMessage)
        .build();
    return createLog(createRequest);
  }

  /**
   * 사용자 로그를 검색어와 페이지네이션으로 조회합니다.
   */
  public List<LogResponseDto> findLogs(Long userId, String search, int page, int size) {
    if (page < 0 || size <= 0) {
      throw new IllegalArgumentException("page는 0 이상, size는 1 이상이어야 합니다.");
    }
    int offset = Math.max(page, 0) * size;
    List<Log> logs = loggerMapper.findLogs(userId, search, size, offset);
    return logs.stream()
        .map(LogResponseDto::from)
        .toList();
  }

  /**
   * 사용자별 로그 타입 카운트를 반환합니다.
   */
  public Map<LogType, Long> countLogsByType(Long userId) {
    List<LogTypeCountRow> rows = loggerMapper.countLogsByType(userId);
    Map<LogType, Long> counts = new EnumMap<>(LogType.class);
    for (LogTypeCountRow row : rows) {
      LogType type = LogType.fromId(row.getTypeId());
      if (type != null) {
        counts.put(type, row.getCount());
      }
    }
    return counts;
  }

  /**
   * 특정 jobId 스트림을 시작합니다. 사용자 불일치 시 예외를 던집니다.
   */
  public SseEmitter streamLogs(String jobId, Long fromId, Long userId) {
    List<Log> logs = loggerMapper.findByJobIdAfterId(jobId, fromId);
    validateStreamAccess(logs, userId);

    SseEmitter emitter = new SseEmitter();
    // 초반 응답은 비동기로 밀어줘 클라이언트 연결 성능 확보
    taskExecutor.execute(() -> {
      try {
        for (Log log : logs) {
          emitter.send(LogResponseDto.from(log));
        }
        emitter.complete();
      } catch (IOException e) {
        emitter.completeWithError(e);
      }
    });

    return emitter;
  }

  private void validateStreamAccess(List<Log> logs, Long userId) {
    if (logs.isEmpty()) {
      return;
    }
    boolean validUser = logs.stream()
        .allMatch(log -> log.getUserId() != null && log.getUserId().equals(userId));
    if (!validUser) {
      throw new IllegalArgumentException("해당 사용자에게 접근 권한이 없습니다.");
    }
  }

  private String formatPipelineMessage(PipelineLogCreateRequest request) {
    String dateText = request.getLoggedDate() != null
        ? request.getLoggedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        : "";
    String base = request.getLoggedProcess() + " | " + dateText + " | " + request.getMessage();
    if (request.getSubmessage() == null || request.getSubmessage().isBlank()) {
      return base;
    }
    return base + " \n\t" + request.getSubmessage();
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
