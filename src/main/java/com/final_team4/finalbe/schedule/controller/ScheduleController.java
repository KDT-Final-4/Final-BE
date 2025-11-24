package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe.schedule.dto.schedule.*;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDetailResponseDto> findAll(@RequestParam Long userId) {
        return scheduleService.findAll(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleDetailResponseDto findById(@RequestParam Long userId, @PathVariable Long id) {
        return scheduleService.findById(userId, id);
    }

    // 추후 JWT 도입 후 사용자의 정보를 가져와 사용할 예정
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleCreateResponseDto create(@RequestBody ScheduleCreateRequestDto scheduleCreateRequestDto) {
        return scheduleService.insert(scheduleCreateRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleUpdateResponseDto update(@RequestParam Long userId, @PathVariable Long id, @RequestBody ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        return scheduleService.update(userId, id, scheduleUpdateRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @PathVariable Long id) {
        scheduleService.deleteById(userId, id);
    }
}
