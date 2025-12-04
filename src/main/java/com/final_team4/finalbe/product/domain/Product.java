package com.final_team4.finalbe.product.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private String link;
    private String thumbnail;
    private Long price;
    private LocalDateTime createdAt;
}
