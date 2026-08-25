package com.example.springbootpractice.domain.gift.dto;

import java.math.BigDecimal;

public record GiftProductCreateRequest(
        String productName,
        String brandName,
        BigDecimal price
) {
}
