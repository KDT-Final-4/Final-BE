package com.final_team4.finalbe._core.jwt;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

  private String issuer;
  private String secret;
  private Duration tempValidity = Duration.ofHours(2);

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Duration getTempValidity() {
    return tempValidity;
  }

  public void setTempValidity(Duration tempValidity) {
    this.tempValidity = tempValidity;
  }
}
