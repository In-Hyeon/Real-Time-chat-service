package com.example.springbootpractice.domain.gift.service;

import com.example.springbootpractice.domain.gift.dto.GiftOrderResponse;
import com.example.springbootpractice.domain.gift.entity.GiftOrder;
import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import com.example.springbootpractice.domain.gift.repository.GiftOrderRepository;
import com.example.springbootpractice.domain.gift.repository.GiftProductRepository;
import com.example.springbootpractice.domain.gift.repository.VoucherRepository;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GiftService {

    private static final long VOUCHER_VALID_DAYS = 30;

    private final GiftProductRepository giftProductRepository;
    private final GiftOrderRepository giftOrderRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    @Transactional
    public GiftProduct createProduct(String productName, String brandName, BigDecimal price) {
        return giftProductRepository.save(GiftProduct.create(productName, brandName, price));
    }

    public List<GiftProduct> findAllProducts() {
        return giftProductRepository.findAll();
    }

    @Transactional
    public Voucher order(Long senderId, Long receiverUserId, Long productId) {
        if (senderId.equals(receiverUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_GIFT_SELF);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        GiftProduct product = giftProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GIFT_PRODUCT_NOT_FOUND));

        GiftOrder order = giftOrderRepository.save(GiftOrder.create(sender, receiver, product));

        String voucherCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        LocalDateTime validUntil = LocalDateTime.now().plusDays(VOUCHER_VALID_DAYS);
        return voucherRepository.save(Voucher.create(order, voucherCode, validUntil));
    }

    public List<GiftOrderResponse> findSentOrders(Long userId) {
        return toResponses(giftOrderRepository.findAllBySenderId(userId));
    }

    public List<GiftOrderResponse> findReceivedOrders(Long userId) {
        return toResponses(giftOrderRepository.findAllByReceiverId(userId));
    }

    @Transactional
    public Voucher useVoucher(Long userId, Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

        if (!voucher.getOrder().getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.VOUCHER_NOT_FOUND);
        }
        if (!voucher.isUsable()) {
            throw new BusinessException(ErrorCode.VOUCHER_ALREADY_USED);
        }

        voucher.use();
        return voucher;
    }

    private List<GiftOrderResponse> toResponses(List<GiftOrder> orders) {
        List<Long> orderIds = orders.stream().map(GiftOrder::getId).toList();
        Map<Long, Voucher> vouchersByOrderId = voucherRepository.findAllByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(v -> v.getOrder().getId(), Function.identity()));

        return orders.stream()
                .map(order -> GiftOrderResponse.of(order, vouchersByOrderId.get(order.getId())))
                .toList();
    }
}
