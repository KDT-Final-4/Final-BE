package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.schedule.dto.schedule.*;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDetailResponseDto> findAll(@AuthenticationPrincipal JwtPrincipal principal) {
        return scheduleService.findAll(principal.userId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDetailResponseDto findById(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long id) {
        return scheduleService.findById(principal.userId(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleCreateResponseDto create(@AuthenticationPrincipal JwtPrincipal principal, @RequestBody @Valid ScheduleCreateRequestDto scheduleCreateRequestDto) {
        return scheduleService.insert(principal.userId(), scheduleCreateRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleUpdateResponseDto update(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long id, @RequestBody @Valid ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        return scheduleService.update(principal.userId(), id, scheduleUpdateRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal JwtPrincipal principal, @PathVariable Long id) {
        scheduleService.deleteById(principal.userId(), id);
    }
}
