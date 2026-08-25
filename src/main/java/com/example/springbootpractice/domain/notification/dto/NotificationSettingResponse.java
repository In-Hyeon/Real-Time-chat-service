package com.example.springbootpractice.domain.notification.dto;

import com.example.springbootpractice.domain.notification.entity.NotificationSetting;
import java.time.LocalTime;

public record NotificationSettingResponse(
        boolean isEnabled,
        boolean isSound,
        LocalTime quietStartTime,
        LocalTime quietEndTime
) {
    public static NotificationSettingResponse from(NotificationSetting setting) {
        return new NotificationSettingResponse(
                setting.isEnabled(), setting.isSound(), setting.getQuietStartTime(), setting.getQuietEndTime());
    }
}
