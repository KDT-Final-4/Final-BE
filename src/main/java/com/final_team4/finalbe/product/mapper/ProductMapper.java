package com.final_team4.finalbe.product.mapper;

import com.final_team4.finalbe.product.domain.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    void insert(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategoryId(Long id);
}
