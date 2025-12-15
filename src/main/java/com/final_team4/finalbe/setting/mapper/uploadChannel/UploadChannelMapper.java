package com.final_team4.finalbe.setting.mapper.uploadChannel;

import com.final_team4.finalbe.setting.domain.uploadChannel.UploadChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UploadChannelMapper {
    // 유저 ID로 채널 정보 조회
    List<UploadChannel> findByUserId(@Param("userId") Long userId);

    // 채널 ID로 채널 정보 단건 조회
    UploadChannel findById(@Param("id") Long id);
}
