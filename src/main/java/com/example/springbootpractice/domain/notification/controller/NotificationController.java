package com.example.springbootpractice.domain.notification.controller;

import com.example.springbootpractice.domain.notification.dto.DeviceTokenRegisterRequest;
import com.example.springbootpractice.domain.notification.dto.DeviceTokenResponse;
import com.example.springbootpractice.domain.notification.dto.NotificationSettingResponse;
import com.example.springbootpractice.domain.notification.dto.NotificationSettingUpdateRequest;
import com.example.springbootpractice.domain.notification.service.NotificationService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/device-tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DeviceTokenResponse> registerDeviceToken(@AuthenticationPrincipal Long userId,
                                                                  @RequestBody DeviceTokenRegisterRequest request) {
        var token = notificationService.registerDeviceToken(userId, request.deviceToken(), request.deviceType());
        return ApiResponse.success(DeviceTokenResponse.from(token));
    }

    @GetMapping("/device-tokens")
    public ApiResponse<List<DeviceTokenResponse>> myDeviceTokens(@AuthenticationPrincipal Long userId) {
        List<DeviceTokenResponse> tokens = notificationService.findMyDeviceTokens(userId).stream()
                .map(DeviceTokenResponse::from)
                .toList();
        return ApiResponse.success(tokens);
    }

    @DeleteMapping("/device-tokens/{id}")
    public ApiResponse<Void> removeDeviceToken(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        notificationService.removeDeviceToken(userId, id);
        return ApiResponse.success(null);
    }

    @GetMapping("/settings")
    public ApiResponse<NotificationSettingResponse> mySettings(@AuthenticationPrincipal Long userId) {
        var setting = notificationService.getMySettings(userId);
        return ApiResponse.success(NotificationSettingResponse.from(setting));
    }

    @PatchMapping("/settings")
    public ApiResponse<NotificationSettingResponse> updateMySettings(@AuthenticationPrincipal Long userId,
                                                                       @RequestBody NotificationSettingUpdateRequest request) {
        var setting = notificationService.updateMySettings(userId, request.isEnabled(), request.isSound(),
                request.quietStartTime(), request.quietEndTime());
        return ApiResponse.success(NotificationSettingResponse.from(setting));
    }
}
