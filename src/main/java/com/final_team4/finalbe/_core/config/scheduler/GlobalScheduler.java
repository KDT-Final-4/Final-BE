package com.final_team4.finalbe._core.config.scheduler;

import com.final_team4.finalbe.restClient.service.RestClientCallerService;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalScheduler {

    private final ScheduleService scheduleService;

    private final RestClientCallerService restService;

    @Scheduled(cron = "0 0/1 * * * *")
    public void schedule() {
        scheduleService.processDueSchedules();
    }

    @Scheduled(cron = "0 0 * * * * ")
    public void searchTrend() {
      restService.callGetKeywords();
    }
}
