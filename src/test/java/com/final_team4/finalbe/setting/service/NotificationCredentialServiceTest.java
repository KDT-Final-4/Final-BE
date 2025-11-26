package com.final_team4.finalbe.setting.service;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import com.final_team4.finalbe.setting.dto.notificationCredential.NotificationCredentialCreateRequestDto;
import com.final_team4.finalbe.setting.dto.notificationCredential.NotificationCredentialCreateResponseDto;
import com.final_team4.finalbe.setting.dto.notificationCredential.NotificationCredentialDetailResponseDto;
import com.final_team4.finalbe.setting.dto.notificationCredential.NotificationCredentialUpdateRequestDto;
import com.final_team4.finalbe.setting.dto.notificationCredential.NotificationCredentialUpdateResponseDto;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class NotificationCredentialServiceTest {

    private static final long USER_ID = 2L;
    private static final long CHANNEL_ID = 2L;

    @Autowired
    private NotificationCredentialService notificationCredentialService;

    @Autowired
    private NotificationCredentialMapper notificationCredentialMapper;

    @Test
    void findById() {
        // given
        NotificationCredential entity = NotificationCredential.builder()
                .userId(USER_ID)
                .channelId(CHANNEL_ID)
                .webhookUrl("webhookUrl")
                .apiToken("apiToken")
                .isActive(true)
                .build();

        notificationCredentialMapper.insert(entity);
        Long id = entity.getId();

        // when
        NotificationCredentialDetailResponseDto dto = notificationCredentialService.findById(USER_ID, id);

        // then
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUserId()).isEqualTo(USER_ID);
        assertThat(dto.getWebhookUrl()).isEqualTo(entity.getWebhookUrl());
        assertThat(dto.getApiToken()).isEqualTo(entity.getApiToken());
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    void insert() {
        // given
        NotificationCredentialCreateRequestDto request = NotificationCredentialCreateRequestDto.builder()
                .channelId(CHANNEL_ID)
                .webhookUrl("insertWebhook")
                .apiToken("insertToken")
                .isActive(true)
                .build();

        // when
        NotificationCredentialCreateResponseDto response = notificationCredentialService.insert(USER_ID, request);

        // then
        assertThat(response.getWebhookUrl()).isEqualTo("insertWebhook");
        assertThat(response.getApiToken()).isEqualTo("insertToken");
        assertThat(response.getIsActive()).isTrue();
    }

    @Test
    void findByChannelId() {
        // given
        NotificationCredential entity = NotificationCredential.builder()
                .userId(USER_ID)
                .channelId(CHANNEL_ID)
                .webhookUrl("webhookUrl")
                .apiToken("apiToken")
                .isActive(true)
                .build();

        notificationCredentialMapper.insert(entity);

        // when
        NotificationCredentialDetailResponseDto dto = notificationCredentialService.findByChannelId(USER_ID, CHANNEL_ID);

        // then
        assertThat(dto.getUserId()).isEqualTo(USER_ID);
        assertThat(dto.getWebhookUrl()).isEqualTo(entity.getWebhookUrl());
        assertThat(dto.getApiToken()).isEqualTo(entity.getApiToken());
    }

    @Test
    void findByChannelId_throws_when_not_found() {
        // given
        long unknownChannel = 999L;

        // when & then
        assertThatThrownBy(() -> notificationCredentialService.findByChannelId(USER_ID, unknownChannel))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessageContaining("알림 설정을 찾을 수 없습니다");
    }

    @Test
    void update() {
        // given
        NotificationCredential existing = NotificationCredential.builder()
                .userId(USER_ID)
                .channelId(CHANNEL_ID)
                .webhookUrl("oldWebhook")
                .apiToken("oldToken")
                .isActive(true)
                .build();
        notificationCredentialMapper.insert(existing);

        NotificationCredentialUpdateRequestDto request = NotificationCredentialUpdateRequestDto.builder()
                .channelId(existing.getChannelId())
                .webhookUrl("newWebhook")
                .apiToken("newToken")
                .isActive(false)
                .build();

        // when
        NotificationCredentialUpdateResponseDto response = notificationCredentialService.update(USER_ID, existing.getId(), request);
        NotificationCredentialDetailResponseDto refreshed = notificationCredentialService.findById(USER_ID, existing.getId());

        // then
        assertThat(response.getId()).isEqualTo(existing.getId());
        assertThat(response.getWebhookUrl()).isEqualTo("newWebhook");
        assertThat(response.getApiToken()).isEqualTo("newToken");
        assertThat(response.getIsActive()).isFalse();
        assertThat(refreshed.getWebhookUrl()).isEqualTo("newWebhook");
        assertThat(refreshed.getApiToken()).isEqualTo("newToken");
        assertThat(refreshed.getIsActive()).isFalse();
    }

    @Test
    void updateByActive() {
        // given
        NotificationCredentialCreateRequestDto request = NotificationCredentialCreateRequestDto.builder()
                .channelId(CHANNEL_ID)
                .webhookUrl("activeWebhook")
                .apiToken("activeToken")
                .isActive(true)
                .build();

        NotificationCredentialCreateResponseDto created = notificationCredentialService.insert(USER_ID, request);

        // when
        int result = notificationCredentialService.updateByActive(USER_ID, created.getId(), false);
        NotificationCredentialDetailResponseDto refreshed = notificationCredentialService.findById(USER_ID, created.getId());

        // then
        assertThat(result).isEqualTo(1);
        assertThat(refreshed.getIsActive()).isFalse();
    }

    @Test
    void updateByActive_throws_when_not_found() {
        // when & then
        assertThatThrownBy(() -> notificationCredentialService.updateByActive(USER_ID, 999L, false))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessageContaining("알림 설정을 찾을 수 없습니다");
    }
}
