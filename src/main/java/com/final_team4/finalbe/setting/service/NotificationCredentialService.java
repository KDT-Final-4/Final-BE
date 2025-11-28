package com.final_team4.finalbe.setting.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import com.final_team4.finalbe.setting.dto.notificationCredential.*;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationCredentialService {

    private final NotificationCredentialMapper notificationMapper;

    public NotificationCredentialDetailResponseDto findById(Long userId, Long id) {
        NotificationCredential entity = notificationMapper.findById(userId, id);
        if (entity == null) {
            throw new ContentNotFoundException("알림 설정을 찾을 수 없습니다.");
        }
        return NotificationCredentialDetailResponseDto.from(entity);
    }

    public NotificationCredentialDetailResponseDto findByChannelId(Long userId, Long channelId) {
        NotificationCredential entity = notificationMapper.findByChannelId(userId, channelId);

        if (entity == null) {
            throw new ContentNotFoundException("알림 설정을 찾을 수 없습니다.");
        }

        return NotificationCredentialDetailResponseDto.from(entity);
    }

    public NotificationCredentialUpdateResponseDto update(Long userId, Long id, NotificationCredentialUpdateRequestDto requestDto) {

        NotificationCredential entity = notificationMapper.findById(userId, id);

        if (entity == null) {
            throw new ContentNotFoundException("알림 설정을 찾을 수 없습니다.");
        }
        entity.update(
                requestDto.getWebhookUrl(),
                requestDto.getApiToken(),
                requestDto.getIsActive()
        );

        notificationMapper.update(entity);

        return NotificationCredentialUpdateResponseDto.from(entity);
    }

    public NotificationCredentialCreateResponseDto insert(Long userId, NotificationCredentialCreateRequestDto requestDto) {
        NotificationCredential entity = requestDto.toEntity(userId);
        notificationMapper.insert(entity);
        return NotificationCredentialCreateResponseDto.from(entity);
    }

    public int updateByActive(Long userId, Long id, Boolean isActive) {
        NotificationCredential entity = notificationMapper.findById(userId, id);

        if (entity == null) {
            throw new ContentNotFoundException("알림 설정을 찾을 수 없습니다.");
        }

        return notificationMapper.updateByActive(userId, id, isActive);

    }

    public Long findActiveChannelIdByUserId(Long userId) {
        Long activeChannelIdByUserId = notificationMapper.findActiveChannelIdByUserId(userId);
        if (activeChannelIdByUserId == null) {
            throw new ContentNotFoundException("활성화된 설정을 찾을 수 없습니다.");
        }
        return activeChannelIdByUserId;
    }
}
