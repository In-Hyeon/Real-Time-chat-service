package com.example.springbootpractice.domain.notification.dto;

import com.example.springbootpractice.domain.notification.entity.DeviceToken;
import java.time.LocalDateTime;

public record DeviceTokenResponse(
        Long id,
        String deviceToken,
        String deviceType,
        LocalDateTime updatedAt
) {
    public static DeviceTokenResponse from(DeviceToken token) {
        return new DeviceTokenResponse(token.getId(), token.getDeviceToken(), token.getDeviceType(), token.getUpdatedAt());
    }
}
