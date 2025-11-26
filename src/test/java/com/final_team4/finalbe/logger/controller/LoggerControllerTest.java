package com.final_team4.finalbe.logger.controller;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LoggerControllerTest {
  /*
    엔드포인트: /api/log [POST]
    입력 Body:
      {
        id: 0,
        message: "로그 메세지",
        logType: "INFO",
        loggedProcess: "Pipeline",
        loggedDate 2025-11-19T14:32:25.00,
        jobId: "1923847123",
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
      jobId: String, 검색어
      page: int, 페이지 번호
      size: int, 페이지 크기
    동작: 전체 로그 페이지네이션하여 검색어에 따라 조회
    출력:
      [
        {
          id: 1,
          logType: "INFO",
          message: "로그 메세지",
          submessage: "50개 상품 수집, 1개 선택 됨",
          jobId: "1923847123",
          loggedDate 2025-11-19T14:32:25.00,
        },
        {
          id: 2,
          logType: "INFO",
          message: "로그 메세지",
          submessage: "50개 상품 수집, 1개 선택 됨"
          jobId: "1923847123",
   loggedDate "2025-11-19T14:32:25.00",
       },
       ...
      ]
   */

  /*

   */
}
