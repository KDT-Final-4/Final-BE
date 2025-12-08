package com.final_team4.finalbe.setting.controller.notification;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.setting.dto.notification.NotificationCredentialDetailResponseDto;
import com.final_team4.finalbe.setting.dto.notification.NotificationCredentialUpdateRequestDto;
import com.final_team4.finalbe.setting.dto.notification.NotificationCredentialUpdateResponseDto;
import com.final_team4.finalbe.setting.service.notification.NotificationCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting/notification")
public class NotificationCredentialController {

    private final NotificationCredentialService notificationCredentialService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public NotificationCredentialDetailResponseDto getNotificationCredentialDetail(@AuthenticationPrincipal JwtPrincipal user) {
        return notificationCredentialService.findByUserId(user.userId());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationCredentialUpdateResponseDto updateNotificationCredentialDetail(@AuthenticationPrincipal JwtPrincipal user, @PathVariable Long id, @RequestBody NotificationCredentialUpdateRequestDto updateRequestDto) {

        return notificationCredentialService.update(user.userId(), id, updateRequestDto);
    }
}
