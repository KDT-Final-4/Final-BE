package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import com.final_team4.finalbe.notification.vo.NotificationWithTypeAndChannelAndCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackService {

    private final RestClient restClient;
    private final NotificationMapper notificationMapper;

    public void sendNotification(Long userId, Long id) {

        NotificationWithTypeAndChannelAndCredential dto = notificationMapper.findByIdWithTypeAndChannelAndCredential(userId, id);

        if(dto == null || dto.getChannelId() == null) {
            throw new ContentNotFoundException("알림 설정이 없거나 혹은 엑티브 상태의 알림이 존재하지 않습니다.");
        }

        if (dto.getChannelName()  == null || !dto.getChannelName().equals("SLACK")) {
            throw new ContentNotFoundException("잘못된 체널입니다.");
        }

        if (dto.getCredentialWebhook() == null || dto.getCredentialWebhook().isEmpty() || dto.getCredentialWebhook().isBlank()) {
            throw new ContentNotFoundException("WEBHOOK URL이 존재하지 않습니다.");
        }

        restClient.post()
                .uri(dto.getCredentialWebhook())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("text", dto.getTitle() != null ?  dto.getTitle() : dto.getTypeDescription()))
                .retrieve()
                .toBodilessEntity();
    }

//    public String validateUri(String uri) {
//
//    }
}
