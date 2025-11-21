package com.final_team4.finalbe._core.jwt;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

  private String issuer;
  private String secret;
  private Duration tempValidity = Duration.ofHours(2);

}
