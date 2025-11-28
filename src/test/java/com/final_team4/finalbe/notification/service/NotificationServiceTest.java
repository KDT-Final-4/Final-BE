package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe.notification.dto.NotificationCreateRequestDto;
import com.final_team4.finalbe.notification.dto.NotificationCreateResponseDto;
import com.final_team4.finalbe.notification.dto.NotificationDetailResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class NotificationServiceTest {
    // 1. 알림 생성
    // 2. 알림 전송
    // 3. 알림 조회

    @Autowired
    private NotificationService notificationService;

    @Test
    @DisplayName("성공_알림 생성 및 저장")
    void insert() {
        // given
        Long userId = 1L;
        NotificationCreateRequestDto dto = NotificationCreateRequestDto.builder()
                .title("test")
                .contentId(3L)
                .message("test")
                .notificationLevel(1L)
                .typeId(1L)
                .build();


        // when
        NotificationCreateResponseDto responseDto = notificationService.insert(userId, dto);

        // then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTypeId()).isEqualTo(dto.getTypeId());
        assertThat(responseDto.getContentId()).isEqualTo(dto.getContentId());
        assertThat(responseDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(responseDto.getMessage()).isEqualTo(dto.getMessage());
        assertThat(responseDto.getNotificationLevel()).isEqualTo(dto.getNotificationLevel());
    }

    @Test
    @DisplayName("성공_알림 전송")
    void send() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("성공_알림 단일 조회")
    void findById() {
        // given
        Long userId = 1L;
        NotificationCreateRequestDto dto = NotificationCreateRequestDto.builder()
                .title("test")
                .contentId(3L)
                .message("test")
                .notificationLevel(1L)
                .typeId(1L)
                .build();
        NotificationCreateResponseDto saved = notificationService.insert(userId, dto);

        // when
        NotificationDetailResponseDto found = notificationService.findById(userId, saved.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getTypeId()).isEqualTo(dto.getTypeId());
        assertThat(found.getContentId()).isEqualTo(dto.getContentId());
        assertThat(found.getTitle()).isEqualTo(dto.getTitle());
        assertThat(found.getMessage()).isEqualTo(dto.getMessage());
        assertThat(found.getNotificationLevel()).isEqualTo(dto.getNotificationLevel());
        assertThat(found.getCreatedAt()).isNotNull();
    }

}
