package com.final_team4.finalbe.content.service;

import com.final_team4.finalbe.content.domain.*;
import com.final_team4.finalbe.content.dto.*;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentMapper contentMapper;
    private final UploadChannelMapper uploadChannelMapper;

    // 검수할 컨텐츠 목록 조회
    public List<ContentListResponse> getContents(Long userId, int page, int size) {
        int offset = page * size;
        List<Content> contents = contentMapper.findAll(userId, size, offset);
        return contents.stream()
                .map(ContentListResponse::from)
                .toList();
    }

    // 컨텐츠 상세 조회
    public ContentDetailResponse getContentDetail(Long userId, Long id) {
        Content content = contentMapper.findById(userId, id);
        return ContentDetailResponse.from(content);
    }

    // 컨텐츠 등록(파이썬에서 호출)
    @Transactional
    public ContentCreateResponse createContent(ContentCreateRequest request) {
        // 1. 채널 조회 및 소유권 검증
        UploadChannel channel = uploadChannelMapper.findById(request.getUploadChannelId());
        if (channel == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 채널입니다: " + request.getUploadChannelId());
        }

        if (!channel.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("해당 채널에 대한 권한이 없습니다.");
        }

        // 2. Content 생성
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
