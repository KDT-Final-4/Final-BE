package com.final_team4.finalbe._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class TaskExecutorConfig {
  @Bean
  public TaskExecutor virtualThreadTaskExecutor() {
    return new VirtualThreadTaskExecutor();
  }
}
