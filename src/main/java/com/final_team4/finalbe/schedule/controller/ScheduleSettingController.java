package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingDetailResponseDto;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingUpdateRequestDto;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingUpdateResponseDto;
import com.final_team4.finalbe.schedule.service.ScheduleSettingService;
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
    public ScheduleSettingUpdateResponseDto update(@AuthenticationPrincipal JwtPrincipal user, @PathVariable Long id, @RequestBody @Valid ScheduleSettingUpdateRequestDto requestDto) {
        if(user == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return scheduleSettingService.update(user.userId(), id, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ScheduleSettingDetailResponseDto findByUserId(@AuthenticationPrincipal JwtPrincipal user) {
        if (user == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return scheduleSettingService.findByUserId(user.userId());
    }
}
