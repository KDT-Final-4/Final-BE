package com.final_team4.finalbe.content.service;

import com.final_team4.finalbe.content.domain.*;
import com.final_team4.finalbe.content.dto.*;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.content.dto.ContentUploadPayloadDto;
import com.final_team4.finalbe.product.dto.ProductCreateRequestDto;
import com.final_team4.finalbe.product.dto.ProductCreateResponseDto;
import com.final_team4.finalbe.product.mapper.ProductContentMapper;
import com.final_team4.finalbe.product.service.ProductService;
import com.final_team4.finalbe.restClient.service.RestClientCallerService;
import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import jakarta.validation.Valid;
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
    private final ProductService productService;
    private final ProductContentMapper productContentMapper;
    private final RestClientCallerService restClientCallerService;

    // 검수할 컨텐츠 목록 조회
    public List<ContentListResponseDto> getContents(Long userId, int page, int size) {
        int offset = page * size;
        List<Content> contents = contentMapper.findAll(userId, size, offset);
        return contents.stream()
                .map(ContentListResponseDto::from)
                .toList();
    }

    // 컨텐츠 상세 조회
    public ContentDetailResponseDto getContentDetail(Long userId, Long id) {
        Content content = getVerifiedContent(userId, id);
        return ContentDetailResponseDto.from(content);
    }

    // 컨텐츠 등록(파이썬에서 호출)
    @Transactional
    public ContentCreateResponseDto createContent(ContentCreateRequestDto request) {
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
                .contentLink(request.getContentLink())   // 추가
                .keyword(request.getKeyword())    // 추가
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        contentMapper.insert(content);

        // product
        ContentProductRequestDto product = request.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("상품 정보가 없습니다.");
        }

        ProductCreateRequestDto productRequest = ProductCreateRequestDto.builder()
                .category(product.getCategory())
                .name(product.getTitle())
                .link(product.getLink())
                .thumbnail(product.getThumbnail())
                .price(product.getPrice())
                .build();

        ProductCreateResponseDto productResponse = productService.create(productRequest);
        productContentMapper.insert(productResponse.getId(), content.getId());

        return ContentCreateResponseDto.from(content);
    }

    // 컨텐츠 수정
    @Transactional
    public ContentUpdateResponseDto updateContent(Long userId, Long id, @Valid ContentUpdateRequestDto request) {
        Content content = getVerifiedContent(userId, id);

        content.updateContent(request.getTitle(), request.getBody());
        contentMapper.update(content);

        return ContentUpdateResponseDto.from(content);
    }

    // 컨텐츠 상태 변경
    @Transactional
    public ContentUpdateResponseDto updateContentStatus(Long userId, Long id, @Valid ContentStatusUpdateRequestDto request) {
        Content content = getVerifiedContent(userId, id);

        if (request.getStatus() == ContentStatus.APPROVED) {
            restClientCallerService.callUploadPosts(ContentUploadPayloadDto.from(content));
        }

        content.updateStatus(request.getStatus());
        contentMapper.updateStatus(content);

        return ContentUpdateResponseDto.from(content);
    }

    private Content getVerifiedContent(Long userId, Long id) {
        Content content = contentMapper.findById(userId, id);
        if (content == null) {
            throw new IllegalArgumentException("존재하지 않는 컨텐츠입니다: " + id);
        }
        return content;
    }
}
