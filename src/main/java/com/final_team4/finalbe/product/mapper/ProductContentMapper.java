package com.final_team4.finalbe.product.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductContentMapper {
    void insert(@Param("productId") Long productId, @Param("contentId") Long contentId);
}
