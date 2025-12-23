package com.final_team4.finalbe.product.dto;

import com.final_team4.finalbe.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductListResponseDto {
    private Long id;
    private Long category;
    private String name;
    private String link;
    private Long price;

    public static ProductListResponseDto from(Product entity) {
        return ProductListResponseDto.builder()
                .id(entity.getId())
                .category(entity.getCategoryId())
                .name(entity.getName())
                .link(entity.getLink())
                .price(entity.getPrice())
                .build();
    }
}
