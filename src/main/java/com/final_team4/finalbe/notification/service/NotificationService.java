package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.dto.NotificationDetailResponseDto;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationCreateResponseDto insert(Long userId, NotificationCreateRequestDto dto) {

        return null;
    }

    public NotificationDetailResponseDto findById(Long userId, Long id) {

        return null;
    }
}
