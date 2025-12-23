package com.final_team4.finalbe.setting.mapper.llm;

import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LlmChannelMapper {

    LlmChannel findById(@Param("userId") Long userId, @Param("id") Long id);

    LlmChannel findByUserId(@Param("userId") Long userId);

    void insert(LlmChannel entity);

    void update(LlmChannel entity);
}

