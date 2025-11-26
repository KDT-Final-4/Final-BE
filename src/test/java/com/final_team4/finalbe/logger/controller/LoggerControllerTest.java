package com.final_team4.finalbe.logger.controller;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LoggerControllerTest {
  /*
    엔드포인트: /api/log [POST]
    입력 Body 예시:
      {
        id: 1,
        user_id: 1,
        message: "로그 메세지",
        logType: "INFO",
        loggedProcess: "END",
        loggedDate "2025-11-19T14:32:25.00", << 이건 그냥 LocaldateTime으로 받으면 될거임 ㅇㅇ
        jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        submessage: "50개 상품 수집, 1개 선택 됨",
      }
    동작: LoggerService.createLog 호출하여 로그 생성
          이때, LogCreateRequestDto.message는 "{loggedProcess} | {loggedDate} | {message} \n\t{submessage}" 형태로 만들어져야 함
    출력: 없음 (200 OK만)
   */


  /*
    엔드포인트: /api/log [GET]
    입력 Parameter:
      search: String, 검색어
      page: int, 페이지 번호
      size: int, 페이지 크기
    동작: 전체 로그 페이지네이션하여 검색어에 따라 조회
          user_id가 자신인 것만 조회해야 하며, AuthenticationPrincipal 어노테이션으로 JWT 토큰을 통해 가져올 수 있음.
            자세한 사용 방법은 ScheduleController 에서 확인할 것.
    출력 예시:
      [
        {
          id: 1,
          logType: "INFO",
          message: "로그 메세지",
          submessage: "50개 상품 수집, 1개 선택 됨",
          jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
          loggedDate 2025-11-19T14:32:25.00,
        },
        {
          id: 2,
          logType: "INFO",
          message: "로그 메세지",
          submessage: "50개 상품 수집, 1개 선택 됨"
          jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
   loggedDate "2025-11-19T14:32:25.00",
       },
       ...
      ]
   */


  /*
    엔드포인트: /api/log/count [GET]
    입력: 없음
    동작: 전체 로그에서 LogType 마다의 개수를 셈
          LogType이 바뀜에 따라 같이 추가되어야 함.
          위와 같이 본인 user_id와 같은 것만 세어야 함
    출력 예시:
      {
        info: 5,
        success: 1,
        warning: 1,
        error: 1,
      }
      현재는 info와 error 밖에 없음
   */

  /*
    엔드포인트: /api/pipeline/{job_id} [GET]
    입력 url:
      job_id: String
    입력 Parameter:
      id: int
    동작: 전체 로그에서 job_id에 해당하는 로그를 스트림 형태로 보여줌 (SseEmitor 사용)
          호출 시에는 job_id에 해당하는 전체 로그를 보여주고, 이후 Log에 추가되는대로 업데이트함
            이때 업데이트는 job_id에 해당하는 로그가 createLog 에서 생성된 것이 감지됐을 때마다를 기준으로 함
            만약 id가 제공됐을 경우, 전체 로그를 보여주는게 아닌 해당 id보다 큰 로그를 전체 조회함
            만약 위에서 사용된 AuthenticationPrincipal로 확인된 user_id와 조회된 로그의 user_id가 다를 경우,
              잘못된 접근 예외를 던짐
            만약 log.message가 'END' 로 시작하는 객체가 감지됐거나 연결이 끊겼을 경우 로깅이 끝났다고 간주하고 스트림을 종료함
    출력 예시:
      [
        {
          id: 1,
          message: "로그 메세지",
          logType: "INFO",
          loggedDate 2025-11-19T14:32:25.00,
          jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
          submessage: "SELECTION | 50개 상품 수집, 1개 선택 됨",
        },
        {
          id: 2,
          message: "로그 메세지",
          logType: "INFO",
          loggedDate "2025-11-19T14:32:25.00",
          jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
          submessage: "READING | 자료 수집 중"
        },
        ...
        {
          id: 50,
          message: "로그 메세지",
          logType: "INFO",
          loggedDate "2025-11-19T14:32:25.00",
          jobId: "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
          submessage: "END | 파이프라인 종료"
        },
      ]
   */
}
