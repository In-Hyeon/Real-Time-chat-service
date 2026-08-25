package com.example.springbootpractice.domain.user.controller;

import com.example.springbootpractice.domain.user.dto.SubscriptionResponse;
import com.example.springbootpractice.domain.user.entity.UserSubscription;
import com.example.springbootpractice.domain.user.service.SubscriptionService;
import com.example.springbootpractice.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private static final long DEFAULT_SUBSCRIPTION_DAYS = 30;

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionResponse> subscribe(@AuthenticationPrincipal Long userId,
                                                         @RequestParam(required = false) Long days) {
        UserSubscription subscription =
                subscriptionService.subscribe(userId, days != null ? days : DEFAULT_SUBSCRIPTION_DAYS);
        return ApiResponse.success(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/me")
    public ApiResponse<SubscriptionResponse> me(@AuthenticationPrincipal Long userId) {
        UserSubscription subscription = subscriptionService.findMy(userId);
        return ApiResponse.success(SubscriptionResponse.from(subscription));
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> cancel(@AuthenticationPrincipal Long userId) {
        subscriptionService.cancel(userId);
        return ApiResponse.success(null);
    }
}
