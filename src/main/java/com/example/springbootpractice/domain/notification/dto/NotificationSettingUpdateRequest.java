package com.example.springbootpractice.domain.notification.dto;

import java.time.LocalTime;

public record NotificationSettingUpdateRequest(
        Boolean isEnabled,
        Boolean isSound,
        LocalTime quietStartTime,
        LocalTime quietEndTime
) {
}
