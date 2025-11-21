package com.final_team4.finalbe;

import com.final_team4.finalbe._core.jwt.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.final_team4.finalbe")
@EnableConfigurationProperties(JwtProperties.class)
public class FinalBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(FinalBeApplication.class, args);
  }

}
