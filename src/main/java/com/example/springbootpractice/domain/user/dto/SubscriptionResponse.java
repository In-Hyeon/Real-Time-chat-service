package com.example.springbootpractice.domain.user.dto;

import com.example.springbootpractice.domain.user.entity.UserSubscription;
import java.time.LocalDateTime;

public record SubscriptionResponse(
        boolean active,
        LocalDateTime expiredAt
) {
    public static SubscriptionResponse from(UserSubscription subscription) {
        return new SubscriptionResponse(subscription.isCurrentlyActive(), subscription.getExpiredAt());
    }

    public static SubscriptionResponse inactive() {
        return new SubscriptionResponse(false, null);
    }
}
