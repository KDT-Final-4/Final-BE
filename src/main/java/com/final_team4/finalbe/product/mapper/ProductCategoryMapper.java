package com.final_team4.finalbe.product.mapper;


import com.final_team4.finalbe.product.domain.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCategoryMapper {
    ProductCategory findByName(String name);
}
