package com.final_team4.finalbe.notification.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.content.dto.ContentDetailResponseDto;
import com.final_team4.finalbe.content.service.ContentService;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import com.final_team4.finalbe.notification.vo.NotificationWithTypeAndChannelAndCredential;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.service.llm.LlmChannelService;
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
    private final ContentService contentService;
    private final LlmChannelService llmChannelService;

    public void sendNotification(Long userId, Long id) {
        NotificationWithTypeAndChannelAndCredential dto = errorCheck(userId, id);
        restClient.post()
                .uri(dto.getCredentialWebhook())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("text", createMessage(dto)))
                .retrieve()
                .toBodilessEntity();
    }

    private String createMessage(NotificationWithTypeAndChannelAndCredential dto) {
        ContentDetailResponseDto contentEntity = contentService.getContentByJobId(dto.getContentJobId());

        String status = getUploadStatusLabel(dto.getUserId());
        String link = getLink(contentEntity);

        return """
            :bell: 알림이 도착했습니다.
            
            
            *현재 상태*
            - %s
            
            
            *콘텐츠 제목*
            - %s
            
            
            *내용을 확인하려면 아래 링크를 클릭해주세요!*
            🔗 %s
            """.formatted(status, contentEntity.getTitle(), link);
    }

    private String getUploadStatusLabel(Long userId) {
        LlmChannelDetailResponseDto llmRequestDto = llmChannelService.findByUserId(userId);

        return (llmRequestDto.getGenerationType() == ContentGenType.MANUAL)
                ? "검수 대기 중"
                : "포스팅 완료";
    }

    private String getLink(ContentDetailResponseDto  contentEntity) {
        return contentEntity.getLink() == null ? "www.aura-ai.site" : contentEntity.getLink();
    }

    private NotificationWithTypeAndChannelAndCredential errorCheck(Long userId, Long id) {

        NotificationWithTypeAndChannelAndCredential dto = notificationMapper.findByIdWithTypeAndChannelAndCredential(userId, id);

        if(dto == null || dto.getChannelId() == null) {
            throw new ContentNotFoundException("알림 설정이 없거나 혹은 엑티브 상태의 알림이 존재하지 않습니다.");
        }

        if (dto.getChannelName()  == null || !dto.getChannelName().equals("SLACK")) {
            throw new ContentNotFoundException("잘못된 체널입니다.");
        }

        if (dto.getCredentialWebhook() == null || dto.getCredentialWebhook().isBlank()) {
            throw new ContentNotFoundException("WEBHOOK URL이 존재하지 않습니다.");
        }

        if (dto.getContentJobId() ==null || dto.getContentJobId().isBlank()) {
            throw new ContentNotFoundException("컨텐트 생성 오류입니다.");
        }
         return dto;
    }

}
