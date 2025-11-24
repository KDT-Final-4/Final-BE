package com.final_team4.finalbe.uploadChannel.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import com.final_team4.finalbe.uploadChannel.dto.UploadChannelItemResponse;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadChannelService {

    private final UploadChannelMapper uploadChannelMapper;

    public List<UploadChannelItemResponse> getChannelsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("해당 유저 아이디를 찾을 수 없습니다.");
        }

        List<UploadChannel> channels = uploadChannelMapper.findByUserId(userId);

        if (channels.isEmpty()) {
            throw new ContentNotFoundException("해당 유저의 업로드 채널을 찾을 수 없습니다.");
        }

        return channels.stream()
                .map(UploadChannelItemResponse::from)
                .toList();
    }
}
