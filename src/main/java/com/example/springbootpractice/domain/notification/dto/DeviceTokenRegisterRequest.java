package com.example.springbootpractice.domain.notification.dto;

public record DeviceTokenRegisterRequest(
        String deviceToken,
        String deviceType
) {
}
