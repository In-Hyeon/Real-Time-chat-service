package com.example.springbootpractice.domain.gift.dto;

import com.example.springbootpractice.domain.gift.entity.GiftOrder;
import com.example.springbootpractice.domain.gift.entity.Voucher;

public record GiftOrderResponse(
        Long orderId,
        Long senderId,
        Long receiverId,
        String productName,
        String orderStatus,
        Long voucherId,
        String voucherCode,
        String voucherStatus
) {
    public static GiftOrderResponse of(GiftOrder order, Voucher voucher) {
        return new GiftOrderResponse(
                order.getId(),
                order.getSender().getId(),
                order.getReceiver().getId(),
                order.getProduct().getProductName(),
                order.getOrderStatus(),
                voucher.getId(),
                voucher.getVoucherCode(),
                voucher.getVoucherStatus()
        );
    }
}
