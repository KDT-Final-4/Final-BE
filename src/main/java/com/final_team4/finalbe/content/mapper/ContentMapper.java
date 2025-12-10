package com.final_team4.finalbe.content.mapper;

import com.final_team4.finalbe.content.domain.Content;
import com.final_team4.finalbe.content.domain.ContentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper {
    List<Content> findAll(@Param("userId") Long userId,
                          @Param("status") ContentStatus status,
                          @Param("limit") int limit,
                          @Param("offset") int offset);

    long countAll(@Param("userId") Long userId,
                  @Param("status") ContentStatus status);

    Content findById(Long userId, Long id);

    void insert(Content content);

    void update(Content content);

    void updateStatus(Content content);

    Content findByJobId(@Param("jobId") String jobId);

    void updateLinkByJobId(@Param("jobId") String jobId, @Param("link") String link);

}
