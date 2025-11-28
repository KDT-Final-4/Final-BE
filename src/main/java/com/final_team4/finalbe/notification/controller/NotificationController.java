package com.final_team4.finalbe.notification.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.service.NotificationService;
import com.final_team4.finalbe.notification.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification/")
public class NotificationController {
    private final NotificationService notificationService;
    private final SlackService slackService;

    @PostMapping
    public void send(@AuthenticationPrincipal JwtPrincipal principal, @RequestBody NotificationCreateRequestDto requestDto) {
        NotificationCreateResponseDto dto = notificationService.insert(principal.userId(), requestDto);
        slackService.sendNotification(principal.userId(), dto.getId());
    }
}
