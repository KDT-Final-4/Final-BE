package com.final_team4.finalbe.content.mapper;

import com.final_team4.finalbe.content.domain.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper {
    List<Content> findAll(Long userId, @Param("limit") int limit, @Param("offset") int offset);

    Content findById(Long userId, Long id);

    void insert(Content content);

    void update(Content content);

    void updateStatus(Content content);

    Content findByJobId(@Param("jobId") String jobId);

    void updateLinkByJobId(@Param("jobId") String jobId, @Param("link") String link);

}
