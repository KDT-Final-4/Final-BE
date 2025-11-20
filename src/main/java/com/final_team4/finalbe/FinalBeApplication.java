package com.final_team4.finalbe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.final_team4.finalbe")
public class FinalBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(FinalBeApplication.class, args);
  }

}
