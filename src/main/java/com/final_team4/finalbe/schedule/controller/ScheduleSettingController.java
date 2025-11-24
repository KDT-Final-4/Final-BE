package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe.schedule.dto.scheduleSetting.*;
import com.final_team4.finalbe.schedule.service.ScheduleSettingService;
import com.final_team4.finalbe.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting/schedule")
public class ScheduleSettingController {

    private final ScheduleSettingService scheduleSettingService;


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleSettingUpdateResponseDto update(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody @Valid ScheduleSettingUpdateRequestDto requestDto) {
        if(user == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return scheduleSettingService.update(user.getId(), id, requestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleSettingDetailResponseDto findById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if(user == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return scheduleSettingService.findById(user.getId(), id);
    }
}
