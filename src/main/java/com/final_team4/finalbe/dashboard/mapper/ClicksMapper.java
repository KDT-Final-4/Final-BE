package com.final_team4.finalbe.dashboard.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClicksMapper {

    long countClicksByProductId(@Param("productId") Long productId);

    long countAllClicks();
}
