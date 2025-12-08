package com.final_team4.finalbe.product.dto;

import com.final_team4.finalbe.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCreateRequestDto {
    @NotBlank
    private String category;

    @NotBlank
    private String name;

    @NotBlank
    private String link;

    private String thumbnail;

    @NotNull
    private Long price;

    public Product toEntity(Long categoryId) {
        return Product.builder()
                .categoryId(categoryId)
                .name(this.name)
                .link(this.link)
                .thumbnail(this.thumbnail)
                .price(this.price)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
