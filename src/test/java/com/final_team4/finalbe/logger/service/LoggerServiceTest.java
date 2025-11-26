package com.final_team4.finalbe.logger.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.logger.domain.type.LogType;
import com.final_team4.finalbe.logger.dto.LogCreateRequestDto;
import com.final_team4.finalbe.logger.dto.LogResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
public class LoggerServiceTest {

  @Autowired
  private LoggerService loggerService;
  /*
    사용할 테이블:
      - log
        - id: number, pk
        - user_id: number, fk with user table
        - type_id: number, fk with log_type table
        - job_id: varchar2(128)
        - message: clob
        - created_at: timestamp(6) with time zone

    변경 사항:
      job_id: number -> varchar2(128)로 변경됨! 수정 필요
   */

  // 로그 생성 테스트
  //  이때, DTO로 id를 제외한 요소를 작성해서 로그를 남길 수도, 특정 입력만 작성해서 로그를 남길 수도 있어야 함
  //  특정 입력만 작성할 경우
  //    user_id, message만 작성
  //    type_id는 1로 고정, id는 새로 만들어야 함
  @DisplayName("로그 생성 - 모든 필드를 입력하면 동일하게 저장된다")
  @Test
  void createLogWithAllFieldsSuccess() {
    // given
    LogCreateRequestDto requestDto = LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.ERROR) // ERROR 타입 시드 값 사용
        .jobId("10")
        .message("full payload log")
        .build();

    // when
    LogResponseDto responseDto = loggerService.createLog(requestDto);

    // then
    assertThat(responseDto.getId()).isNotNull();
    assertThat(responseDto.getUserId()).isEqualTo(1L);
    assertThat(responseDto.getLogType()).isEqualTo(LogType.ERROR);
    assertThat(responseDto.getJobId()).isEqualTo("10");
    assertThat(responseDto.getMessage()).isEqualTo("full payload log");
    assertThat(responseDto.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @DisplayName("로그 생성 - userId와 message만 입력하면 typeId는 1로 저장된다")
  @Test
  void createLogWithMinimalFieldsSetsDefaultTypeId() {
    // given
    LogCreateRequestDto requestDto = LogCreateRequestDto.builder()
        .userId(1L)
        .message("minimal payload log")
        .build();

    // when
    LogResponseDto responseDto = loggerService.createLog(requestDto);

    // then
    assertThat(responseDto.getId()).isNotNull();
    assertThat(responseDto.getUserId()).isEqualTo(1L);
    assertThat(responseDto.getLogType()).isEqualTo(LogType.INFO);
    assertThat(responseDto.getMessage()).isEqualTo("minimal payload log");
  }


  // 로그 단일 조회 테스트
  //  입력받은 id의 로그 하나를 조회함
  @DisplayName("로그 단일 조회 - ID로 저장된 로그를 가져온다")
  @Test
  void findByIdSuccess() {
    // given
    LogResponseDto created = loggerService.createLog(LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.INFO)
        .jobId("20")
        .message("single log message")
        .build());

    // when
    LogResponseDto found = loggerService.findById(created.getId());

    // then
    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getUserId()).isEqualTo(1L);
    assertThat(found.getLogType()).isEqualTo(LogType.INFO);
    assertThat(found.getMessage()).isEqualTo("single log message");
  }


  // 로그 30개 조회 테스트
  //  가장 최신 로그 중, 30개를 조회함
  //  type_id, user_id 별로 조회 가능
  //  오래된 순 정렬해야 함
  @DisplayName("로그 30개 조회 - 최신 30개를 필터링 후 오래된 순으로 반환한다")
  @Test
  void findRecentLogsFiltersByUserAndTypeAndOrdersOldestFirst() {
    // given
    Long userId = 1L;
    LogType logType = LogType.ERROR; // 시드에 존재하는 ERROR 타입
    for (int i = 0; i < 35; i++) {
      loggerService.createLog(LogCreateRequestDto.builder()
          .userId(userId)
          .logType(logType)
          .jobId(String.valueOf(i))
          .message("target log " + i)
          .build());
    }
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(userId)
        .logType(LogType.INFO) // INFO 타입 다른 타입 데이터
        .jobId("99")
        .message("other type log")
        .build());

    // when
    List<LogResponseDto> logs = loggerService.findRecentLogs(userId, logType);

    // then
    assertThat(logs).hasSize(30);
    assertThat(logs.getFirst().getMessage()).isEqualTo("target log 5");
    assertThat(logs.getLast().getMessage()).isEqualTo("target log 34");
    assertThat(logs).extracting(LogResponseDto::getUserId).containsOnly(userId);
    assertThat(logs).extracting(LogResponseDto::getLogType).containsOnly(logType);
    assertThat(logs).isSortedAccordingTo(Comparator.comparing(LogResponseDto::getCreatedAt));
  }


  // JOB ID로 로그 조회
  //  job_id로 검색되는 모든 로우를 조회함
  //  오래된 순 정렬해야 함
  @DisplayName("JOB ID로 로그 조회 - 동일 JOB ID 로그를 오래된 순으로 반환한다")
  @Test
  void findByJobIdSuccess() {
    // given
    String jobId = "777";
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.INFO)
        .jobId(jobId)
        .message("first job log")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.INFO)
        .jobId(jobId)
        .message("second job log")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.INFO)
        .jobId(jobId)
        .message("third job log")
        .build());

    // when
    List<LogResponseDto> logs = loggerService.findByJobId(jobId);

    // then
    assertThat(logs).hasSize(3);
    assertThat(logs).extracting(LogResponseDto::getJobId).containsOnly(jobId);
    assertThat(logs.getFirst().getMessage()).isEqualTo("first job log");
    assertThat(logs.getLast().getMessage()).isEqualTo("third job log");
    assertThat(logs).isSortedAccordingTo(Comparator.comparing(LogResponseDto::getCreatedAt));
  }


  // 해당하는 JOB ID가 없을 경우 로그 조회 실패
  @DisplayName("JOB ID로 로그 조회 실패 - 결과가 없으면 예외를 던진다")
  @Test
  void findByJobIdWhenNotExistsThrowsException() {
    // given
    String missingJobId = "9999";

    // when && then
    assertThatThrownBy(() -> loggerService.findByJobId(missingJobId))
        .isInstanceOf(ContentNotFoundException.class);
  }

  @DisplayName("파이프라인 로그 생성 - 전달 필드를 조합해 메시지를 저장한다")
  @Test
  void createPipelineLog_buildsFormattedMessage() throws Exception {
    // given
    Class<?> payloadClass;
    try {
      payloadClass = Class.forName("com.final_team4.finalbe.logger.dto.PipelineLogCreateRequest");
    } catch (ClassNotFoundException e) {
      fail("PipelineLogCreateRequest DTO가 필요합니다.", e);
      return;
    }

    Object builder = payloadClass.getMethod("builder").invoke(null);
    LocalDateTime loggedDate = LocalDateTime.of(2025, 11, 19, 14, 32, 25);
    builder = builder.getClass().getMethod("userId", Long.class).invoke(builder, 1L);
    builder = builder.getClass().getMethod("logType", LogType.class).invoke(builder, LogType.INFO);
    builder = builder.getClass().getMethod("loggedProcess", String.class).invoke(builder, "END");
    builder = builder.getClass().getMethod("loggedDate", LocalDateTime.class).invoke(builder, loggedDate);
    builder = builder.getClass().getMethod("message", String.class).invoke(builder, "로그 메세지");
    builder = builder.getClass().getMethod("submessage", String.class).invoke(builder, "서브메세지");
    builder = builder.getClass().getMethod("jobId", String.class).invoke(builder, "job-123");
    Object payload = builder.getClass().getMethod("build").invoke(builder);

    // when
    Method createPipelineLog = loggerService.getClass().getMethod("createPipelineLog", payloadClass);
    Object raw = createPipelineLog.invoke(loggerService, payload);
    LogResponseDto response = (LogResponseDto) raw;

    // then
    assertThat(response.getJobId()).isEqualTo("job-123");
    assertThat(response.getLogType()).isEqualTo(LogType.INFO);
    assertThat(response.getMessage())
        .isEqualTo("END | " + loggedDate + " | 로그 메세지 \n\t서브메세지");
  }

  @DisplayName("로그 검색 - 본인 로그만 검색어/페이지네이션으로 조회한다")
  @Test
  void findLogs_withSearchAndPaging_filtersByUserAndKeyword() throws Exception {
    // given
    Long ownerId = 1L;
    Long otherUserId = 2L;
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(ownerId)
        .logType(LogType.INFO)
        .jobId("job-A")
        .message("target keyword first")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(ownerId)
        .logType(LogType.INFO)
        .jobId("job-B")
        .message("another target keyword")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(otherUserId)
        .logType(LogType.ERROR)
        .jobId("job-C")
        .message("target keyword other user")
        .build());

    // when
    Method findLogs = loggerService.getClass().getMethod("findLogs", Long.class, String.class, int.class, int.class);
    @SuppressWarnings("unchecked")
    List<LogResponseDto> logs = (List<LogResponseDto>) findLogs.invoke(loggerService, ownerId, "target", 0, 2);

    // then
    assertThat(logs).hasSize(2);
    assertThat(logs).extracting(LogResponseDto::getUserId).containsOnly(ownerId);
    assertThat(logs).allMatch(log -> log.getMessage().contains("target"));
  }

  @DisplayName("로그 개수 집계 - LogType별 개수를 반환한다")
  @Test
  void countLogsByType_returnsTypeCountsForUser() throws Exception {
    // given
    Long ownerId = 1L;
    Long otherUserId = 2L;
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(ownerId)
        .logType(LogType.INFO)
        .jobId("count-job-1")
        .message("info 1")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(ownerId)
        .logType(LogType.INFO)
        .jobId("count-job-2")
        .message("info 2")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(ownerId)
        .logType(LogType.ERROR)
        .jobId("count-job-3")
        .message("error 1")
        .build());
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(otherUserId)
        .logType(LogType.ERROR)
        .jobId("count-job-4")
        .message("other user error")
        .build());

    // when
    Method countLogsByType = loggerService.getClass().getMethod("countLogsByType", Long.class);
    @SuppressWarnings("unchecked")
    Map<LogType, Long> counts = (Map<LogType, Long>) countLogsByType.invoke(loggerService, ownerId);

    // then
    assertThat(counts.getOrDefault(LogType.INFO, 0L)).isEqualTo(2L);
    assertThat(counts.getOrDefault(LogType.ERROR, 0L)).isEqualTo(1L);
    assertThat(counts.values().stream().mapToLong(Long::longValue).sum()).isEqualTo(3L);
  }

  @DisplayName("파이프라인 스트림 - jobId와 userId로 SSE를 시작한다")
  @Test
  void streamLogsByJobId_startsFromRequestedIdAndValidatesUser() throws Exception {
    // given
    String jobId = "stream-job-1";
    loggerService.createLog(LogCreateRequestDto.builder()
        .userId(1L)
        .logType(LogType.INFO)
        .jobId(jobId)
        .message("first log")
        .build());

    // when
    Method streamLogs = loggerService.getClass().getMethod("streamLogs", String.class, Long.class, Long.class);
    Object rawEmitter = streamLogs.invoke(loggerService, jobId, null, 1L);
    
    // then
    assertThat(rawEmitter).isInstanceOf(SseEmitter.class);

    assertThatThrownBy(() -> streamLogs.invoke(loggerService, jobId, null, 999L))
        .isInstanceOf(Exception.class);
  }
  
}
