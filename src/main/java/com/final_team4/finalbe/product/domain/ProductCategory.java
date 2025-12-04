package com.final_team4.finalbe.product.domain;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {
    private Long id;
    private String name;
    private String description;
}
