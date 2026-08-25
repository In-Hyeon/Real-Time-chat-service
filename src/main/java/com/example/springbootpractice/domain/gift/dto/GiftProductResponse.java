package com.example.springbootpractice.domain.gift.dto;

import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import java.math.BigDecimal;

public record GiftProductResponse(
        Long id,
        String productName,
        String brandName,
        BigDecimal price,
        String imageUrl
) {
    public static GiftProductResponse from(GiftProduct product) {
        return new GiftProductResponse(
                product.getId(), product.getProductName(), product.getBrandName(),
                product.getPrice(), product.getImageUrl());
    }
}
