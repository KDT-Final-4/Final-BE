package com.final_team4.finalbe.link.mapper;

import com.final_team4.finalbe.link.domain.LinkTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkMapper {
    LinkTarget findByJobId(@Param("jobId") String jobId);
    void insertClick(@Param("productId") Long productId, @Param("ip") String ip);
}
