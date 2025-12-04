package com.final_team4.finalbe.product.dto;

import com.final_team4.finalbe.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductCreateResponseDto {
    private Long id;
    private Long category;
    private String name;
    private String link;
    private String thumbnail;
    private Long price;
    private LocalDateTime createdAt;

    public static ProductCreateResponseDto from(Product entity) {
        return ProductCreateResponseDto.builder()
                .id(entity.getId())
                .category(entity.getCategoryId())
                .name(entity.getName())
                .link(entity.getLink())
                .thumbnail(entity.getThumbnail())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
