package com.final_team4.finalbe._core.config.scheduler;

import com.final_team4.finalbe.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalScheduler {

    private final ScheduleService scheduleService;

    @Scheduled(fixedRate = 60000)
    public void schedule() {
        scheduleService.processDueSchedules();
    }

}
