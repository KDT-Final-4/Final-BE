package com.final_team4.finalbe.content.service;

import com.final_team4.finalbe.content.domain.*;
import com.final_team4.finalbe.content.dto.*;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentMapper contentMapper;

    // 컨텐츠 등록(파이썬에서 호출)
    public ContentCreateResponse createContent(ContentCreateRequest request) {

        Content content = Content.builder()
                .jobId(request.getJobId())
                .uploadChannelId(request.getUploadChannelId())
                .userId(request.getUserId())
                .title(request.getTitle())
                .body(request.getBody())
                .status(ContentStatus.PENDING)
                .generationType(ContentGenType.MANUAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        contentMapper.insert(content);

        return ContentCreateResponse.from(content);
    }

}
