package com.final_team4.finalbe.uploadChannel.mapper;

import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UploadChannelMapper {
    List<UploadChannel> findByUserId(@Param("userId") Long userId);
}
