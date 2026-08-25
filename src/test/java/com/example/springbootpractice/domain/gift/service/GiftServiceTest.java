package com.example.springbootpractice.domain.gift.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class GiftServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private GiftService giftService;

    @Test
    void order_성공() {
        User sender = userService.register("gift-a@example.com", "password123", "01077770301");
        User receiver = userService.register("gift-b@example.com", "password123", "01077770302");
        GiftProduct product = giftService.createProduct("아메리카노", "스타벅스", new BigDecimal("4500"));

        Voucher voucher = giftService.order(sender.getId(), receiver.getId(), product.getId());

        assertEquals("UNUSED", voucher.getVoucherStatus());
        assertEquals("SUCCESS", voucher.getOrder().getOrderStatus());
    }

    @Test
    void order_자기자신이면_예외() {
        User user = userService.register("gift-c@example.com", "password123", "01077770303");
        GiftProduct product = giftService.createProduct("케이크", "파리바게트", new BigDecimal("28000"));

        BusinessException e = assertThrows(BusinessException.class,
                () -> giftService.order(user.getId(), user.getId(), product.getId()));
        assertEquals(ErrorCode.CANNOT_GIFT_SELF, e.getErrorCode());
    }

    @Test
    void useVoucher_성공() {
        User sender = userService.register("gift-d@example.com", "password123", "01077770304");
        User receiver = userService.register("gift-e@example.com", "password123", "01077770305");
        GiftProduct product = giftService.createProduct("치킨", "BBQ", new BigDecimal("20000"));
        Voucher voucher = giftService.order(sender.getId(), receiver.getId(), product.getId());

        Voucher used = giftService.useVoucher(receiver.getId(), voucher.getId());

        assertEquals("USED", used.getVoucherStatus());
    }

    @Test
    void useVoucher_이미사용했으면_예외() {
        User sender = userService.register("gift-f@example.com", "password123", "01077770306");
        User receiver = userService.register("gift-g@example.com", "password123", "01077770307");
        GiftProduct product = giftService.createProduct("피자", "도미노", new BigDecimal("25000"));
        Voucher voucher = giftService.order(sender.getId(), receiver.getId(), product.getId());
        giftService.useVoucher(receiver.getId(), voucher.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> giftService.useVoucher(receiver.getId(), voucher.getId()));
        assertEquals(ErrorCode.VOUCHER_ALREADY_USED, e.getErrorCode());
    }

    @Test
    void useVoucher_받는사람이아니면_예외() {
        User sender = userService.register("gift-h@example.com", "password123", "01077770308");
        User receiver = userService.register("gift-i@example.com", "password123", "01077770309");
        User stranger = userService.register("gift-j@example.com", "password123", "01077770310");
        GiftProduct product = giftService.createProduct("초콜릿", "고디바", new BigDecimal("15000"));
        Voucher voucher = giftService.order(sender.getId(), receiver.getId(), product.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> giftService.useVoucher(stranger.getId(), voucher.getId()));
        assertEquals(ErrorCode.VOUCHER_NOT_FOUND, e.getErrorCode());
    }
}
