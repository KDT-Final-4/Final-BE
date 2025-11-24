package com.final_team4.finalbe.restClient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * 다른 서버에 요청을 보내는 작업을 담당하는 서비스입니다.
 * TODO: 클래스 자체에서 파이썬 서버로 어떠한 요청을 보냈다고 로깅할 수 있는 시스템이 필요합니다.
 */
@Service
@RequiredArgsConstructor
public class RestClientCallerService <T>{
  private final RestClient restClient;

  // 키워드 생성해달라고 요청
  public boolean callGetKeywords() {
    ResponseEntity<Void> result = restClient.get()
            .uri("파이썬 쪽 키워드 생성 엔드포인트")
            .retrieve()
            .toBodilessEntity();

    return result.getStatusCode().is2xxSuccessful();
  }

  // 글 생성해달라고 요청
  public boolean callGeneratePosts(T requestData) {
    ResponseEntity<Void> result = restClient.post()
            .uri("파이썬 쪽 글 생성 엔드포인트")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestData)
            .retrieve()
            .toBodilessEntity();

    return result.getStatusCode().is2xxSuccessful();
  }
}
