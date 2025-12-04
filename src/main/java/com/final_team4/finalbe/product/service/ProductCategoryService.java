package com.final_team4.finalbe.product.service;

import com.final_team4.finalbe.product.domain.ProductCategory;
import com.final_team4.finalbe.product.mapper.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    public Long findIdByName(String name) {
        ProductCategory byName = productCategoryMapper.findByName(name);
        if (byName == null || byName.getId() == null) {
            return findIdOfEtcName();
        }
        return byName.getId();
    }

    public Long findIdOfEtcName() {
        ProductCategory byName = productCategoryMapper.findByName("ETC");
        return byName.getId();
    }
}
