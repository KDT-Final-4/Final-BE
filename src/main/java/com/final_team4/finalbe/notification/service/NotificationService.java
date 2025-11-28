package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.notification.domain.Notification;
import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.dto.NotificationDetailResponseDto;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import com.final_team4.finalbe.notification.vo.NotificationWithTypeAndChannelAndCredential;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import com.final_team4.finalbe.setting.service.NotificationCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationCredentialService notificationCredentialService;
    private final RestClient restClient;
    private final NotificationCredentialMapper notificationCredentialMapper;

    @Transactional
    public NotificationCreateResponseDto insert(Long userId, NotificationCreateRequestDto dto) {
        // user 중에서 active인 channel을 가지고 와.
        Long channelId = notificationCredentialService.findActiveChannelIdByUserId(userId);
        //
        Notification entity = dto.toEntity(userId, channelId);
        notificationMapper.insert(entity);
        sendNotification(userId, entity.getId());
        return NotificationCreateResponseDto.from(entity);
    }

    public NotificationDetailResponseDto findById(Long userId, Long id) {

        Notification entity = notificationMapper.findById(userId, id);

        if (entity == null) {
           throw new ContentNotFoundException("알림을 찾을 수 없습니다.");
        }

        return NotificationDetailResponseDto.from(entity);
    }

    public List<NotificationDetailResponseDto> findAllByUserId(Long userId) {
        List<Notification> entities = notificationMapper.findAllByUserId(userId);
        return entities.stream().map(NotificationDetailResponseDto::from).toList();
    }

    public void sendNotification(Long userId, Long id) {

        NotificationWithTypeAndChannelAndCredential dto = notificationMapper.findByIdWithTypeAndChannelAndCredential(userId, id);

        if(dto.getChannelId() == null) {
            throw new ContentNotFoundException("알림 설정이 없거나 혹은 엑티브 상태의 알림이 존재하지 않습니다.");
        }

        switch (dto.getChannelName()) {
            case "EMAIL":
                break;
            case "SLACK":
                restClient.post()
                        .uri(dto.getCredentialWebhook())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("text", dto.getMessage() != null ?  dto.getMessage() : dto.getTypeDescription()))
                        .retrieve()
                        .toBodilessEntity();
                break;
            default:
                break;

        }
    }
}
