package com.example.springbootpractice.domain.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springbootpractice.domain.notification.entity.DeviceToken;
import com.example.springbootpractice.domain.notification.entity.NotificationSetting;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.UserService;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Test
    void registerDeviceToken_같은토큰_재등록시_갱신된다() {
        User user = userService.register("noti-a@example.com", "password123", "01077770401");

        DeviceToken first = notificationService.registerDeviceToken(user.getId(), "token-abc", "ANDROID");
        DeviceToken second = notificationService.registerDeviceToken(user.getId(), "token-abc", "ANDROID");

        assertEquals(first.getId(), second.getId());
        assertEquals(1, notificationService.findMyDeviceTokens(user.getId()).size());
    }

    @Test
    void getMySettings_최초조회시_기본값으로_생성된다() {
        User user = userService.register("noti-b@example.com", "password123", "01077770402");

        NotificationSetting setting = notificationService.getMySettings(user.getId());

        assertTrue(setting.isEnabled());
        assertTrue(setting.isSound());
    }

    @Test
    void updateMySettings_성공() {
        User user = userService.register("noti-c@example.com", "password123", "01077770403");

        NotificationSetting updated = notificationService.updateMySettings(
                user.getId(), false, null, LocalTime.of(22, 0), LocalTime.of(8, 0));

        assertFalse(updated.isEnabled());
        assertTrue(updated.isSound());
        assertEquals(LocalTime.of(22, 0), updated.getQuietStartTime());
    }
}
