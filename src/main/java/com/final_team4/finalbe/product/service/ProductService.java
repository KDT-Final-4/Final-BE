package com.final_team4.finalbe.product.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.product.domain.Product;
import com.final_team4.finalbe.product.dto.ProductCreateRequestDto;
import com.final_team4.finalbe.product.dto.ProductCreateResponseDto;
import com.final_team4.finalbe.product.dto.ProductDetailResponseDto;
import com.final_team4.finalbe.product.dto.ProductListResponseDto;
import com.final_team4.finalbe.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductCategoryService productCategoryService;


    public ProductCreateResponseDto create(ProductCreateRequestDto requestDto) {

        // 카테고리 검사
        Long categoryId = productCategoryService.findIdByName(requestDto.getCategory());

        // 엔티티 변환
        Product entity = requestDto.toEntity(categoryId);

        productMapper.insert(entity);

        return ProductCreateResponseDto.from(entity);
    }

    public ProductDetailResponseDto findById(Long id) {
        Product entity= productMapper.findById(id).orElseThrow(() -> new ContentNotFoundException("제품이 존재하지 않습니다."));
        return ProductDetailResponseDto.from(entity);
    }

    public List<ProductListResponseDto> findAll() {
        List<Product> productList = productMapper.findAll();
        return productList.stream().map(ProductListResponseDto::from).toList();
    }

    public List<ProductListResponseDto> findByCategoryId(Long categoryId) {
        List<Product> entities = productMapper.findByCategoryId(categoryId);
        return entities.stream().map(ProductListResponseDto::from).toList();
    }
}
