package com.example.springbootpractice.domain.notification.service;

import com.example.springbootpractice.domain.notification.entity.DeviceToken;
import com.example.springbootpractice.domain.notification.entity.NotificationSetting;
import com.example.springbootpractice.domain.notification.repository.DeviceTokenRepository;
import com.example.springbootpractice.domain.notification.repository.NotificationSettingRepository;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private static final Set<String> VALID_DEVICE_TYPES = Set.of("IOS", "ANDROID");

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeviceToken registerDeviceToken(Long userId, String deviceToken, String deviceType) {
        if (!VALID_DEVICE_TYPES.contains(deviceType)) {
            throw new BusinessException(ErrorCode.INVALID_DEVICE_TYPE);
        }

        return deviceTokenRepository.findByUserIdAndDeviceToken(userId, deviceToken)
                .map(token -> {
                    token.touch();
                    return token;
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    return deviceTokenRepository.save(DeviceToken.create(user, deviceToken, deviceType));
                });
    }

    public List<DeviceToken> findMyDeviceTokens(Long userId) {
        return deviceTokenRepository.findAllByUserId(userId);
    }

    @Transactional
    public void removeDeviceToken(Long userId, Long deviceTokenId) {
        DeviceToken token = deviceTokenRepository.findById(deviceTokenId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_TOKEN_NOT_FOUND));

        if (!token.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.DEVICE_TOKEN_NOT_FOUND);
        }

        deviceTokenRepository.delete(token);
    }

    @Transactional
    public NotificationSetting getMySettings(Long userId) {
        return notificationSettingRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    return notificationSettingRepository.save(NotificationSetting.create(user));
                });
    }

    @Transactional
    public NotificationSetting updateMySettings(Long userId, Boolean isEnabled, Boolean isSound,
                                                 LocalTime quietStartTime, LocalTime quietEndTime) {
        NotificationSetting setting = getMySettings(userId);
        setting.updateSettings(isEnabled, isSound, quietStartTime, quietEndTime);
        return setting;
    }
}
