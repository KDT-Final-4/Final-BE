package com.final_team4.finalbe.notification.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.service.NotificationService;
import com.final_team4.finalbe.notification.service.SlackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification/")
@Tag(name = "Notification", description = "알림 관리 API")
public class NotificationController {
    private final NotificationService notificationService;
    private final SlackService slackService;

    @Operation(summary = "알림 생성 및 전송", description = "새로운 알림을 생성하고 Slack 으로 전송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림이 성공적으로 생성되고 전송되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "활성화된 알림 설정을 찾을 수 없습니다.")
    })
    @PostMapping
    public void send(@AuthenticationPrincipal JwtPrincipal principal, @RequestBody @Valid NotificationCreateRequestDto requestDto) {
        NotificationCreateResponseDto dto = notificationService.insert(principal.userId(), requestDto);
        slackService.sendNotification(principal.userId(), dto.getId());
    }
}
