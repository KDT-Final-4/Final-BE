package com.final_team4.finalbe._core.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestClientConfig {
  @Value("${python-url}")
  private String baseUrl;

  @Bean
  public RestClient restClient() {
//    var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
//            .setMaxConnPerRoute(4) // 순간 API 요청 수를 줄이기 위해 5개씩 -> 4개씩으로 줄임
//            .build();

//    RequestConfig requestConfig = RequestConfig.custom()
//            .setConnectionRequestTimeout(Timeout.ofMinutes(5))
//            .setResponseTimeout(5, TimeUnit.MINUTES)   // 읽기(응답 대기)
//            .build();

    CloseableHttpClient httpClient = HttpClientBuilder.create()
//            .setConnectionManager(connectionManager)
//            .setDefaultRequestConfig(requestConfig)
            .evictExpiredConnections()
//            .evictIdleConnections(TimeValue.ofMinutes(5))
            .build();

    var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

    return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .build();
  }
}
