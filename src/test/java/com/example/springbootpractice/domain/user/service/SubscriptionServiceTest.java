package com.example.springbootpractice.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.entity.UserSubscription;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SubscriptionServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    void subscribe_성공() {
        User user = userService.register("sub-a@example.com", "password123", "01077770101");

        UserSubscription subscription = subscriptionService.subscribe(user.getId(), 30);

        assertTrue(subscription.isCurrentlyActive());
    }

    @Test
    void findMy_구독한적없으면_예외() {
        User user = userService.register("sub-b@example.com", "password123", "01077770102");

        BusinessException e = assertThrows(BusinessException.class, () -> subscriptionService.findMy(user.getId()));
        assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void cancel_성공() {
        User user = userService.register("sub-c@example.com", "password123", "01077770103");
        subscriptionService.subscribe(user.getId(), 30);

        subscriptionService.cancel(user.getId());

        assertFalse(subscriptionService.findMy(user.getId()).isCurrentlyActive());
    }
}
