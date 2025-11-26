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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  // ------------------------------------------------------
  // 컨트롤러 요구사항 기반 추가 예정 서비스 테스트(주석으로만 작성)
  // RED 단계 참고용이며, 실제 구현 시 각 메서드/DTO가 준비되면 활성화한다.

  // @DisplayName("로그 생성 - 파이프라인 로그 포맷으로 메시지 조립 후 저장된다")
  // @Test
  // void createLog_withPipelinePayload_buildsFormattedMessage() {
  //   // given: loggedProcess, loggedDate, message, submessage, jobId, userId를 포함한 요청 DTO 준비
  //   // when: loggerService.createLog 호출
  //   // then: 저장된 메시지가 "{loggedProcess} | {loggedDate} | {message} \n\t{submessage}" 포맷으로 구성되고 jobId가 문자열로 유지되는지 검증
  // }

  // @DisplayName("로그 검색 - 본인 로그만 검색어/페이지네이션으로 조회한다")
  // @Test
  // void findLogs_withSearchAndPaging_filtersByUserAndKeyword() {
  //   // given: userId와 다양한 로그 메시지/서브메시지를 시드해두고 search 키워드와 page/size 제공
  //   // when: loggerService.findLogs(userId, search, pageRequest) 등 호출
  //   // then: userId가 일치하는 로그만, 검색어가 message 또는 submessage에 포함된 것만 반환하며 페이지 크기/순서가 맞는지 확인
  // }

  // @DisplayName("로그 개수 집계 - LogType별 개수를 반환한다")
  // @Test
  // void countLogsByType_returnsTypeCountsForUser() {
  //   // given: INFO/ERROR 등 서로 다른 타입의 로그를 사용자별로 저장
  //   // when: loggerService.countLogsByType(userId) 호출
  //   // then: 각 타입별 개수가 Map 또는 DTO로 반환되고, 다른 사용자 로그는 집계에 포함되지 않음을 검증
  // }

  // @DisplayName("파이프라인 로그 스트림 - jobId와 id 기준으로 SSE를 시작한다")
  // @Test
  // void streamLogsByJobId_startsFromRequestedIdAndValidatesUser() {
  //   // given: 특정 jobId와 userId로 여러 로그를 저장하고, 기존 마지막 id보다 큰 로그만 조회하는 요청 준비
  //   // when: loggerService.streamLogs(jobId, fromId, userId) 호출 (SseEmitter 반환 예상)
  //   // then: 초기 응답에 기존 로그가 전달되고, userId가 다르면 예외를 던지며, message가 'END'로 시작하면 스트림이 완료되는지 검증
  // }
  
}
