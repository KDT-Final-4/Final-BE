package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.notification.domain.Notification;
import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.dto.NotificationDetailResponseDto;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import com.final_team4.finalbe.setting.service.NotificationCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationCredentialService notificationCredentialService;

    @Transactional
    public NotificationCreateResponseDto insert(Long userId, NotificationCreateRequestDto dto) {
        // user 중에서 active인 channel을 가지고 와.
        Long channelId = notificationCredentialService.findActiveChannelIdByUserId(userId);
        Notification entity = dto.toEntity(userId, channelId);
        notificationMapper.insert(entity);
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


}
