package com.final_team4.finalbe.product.dto;

import com.final_team4.finalbe.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class ProductDetailResponseDto {
    private Long id;
    private Long category;
    private String name;
    private String link;
    private String thumbnail;
    private Long price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static ProductDetailResponseDto from(Product entity) {
        return ProductDetailResponseDto.builder()
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
