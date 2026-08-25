package com.example.springbootpractice.domain.gift.dto;

public record GiftOrderCreateRequest(
        Long receiverUserId,
        Long productId
) {
}
