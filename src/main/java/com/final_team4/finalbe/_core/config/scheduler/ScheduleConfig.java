package com.final_team4.finalbe._core.config.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Bean
    public ThreadPoolTaskExecutor scheduleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // 동시에 처리할 수 있는 스케줄 실행 수
        executor.setMaxPoolSize(10);        // 최대 스레드 수
        executor.setQueueCapacity(50);      // 대기열 크기
        executor.setThreadNamePrefix("schedule-exec-");
        executor.initialize();
        return executor;
    }
}
