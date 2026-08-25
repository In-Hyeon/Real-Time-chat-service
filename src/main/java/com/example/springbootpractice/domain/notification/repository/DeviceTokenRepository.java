package com.example.springbootpractice.domain.notification.repository;

import com.example.springbootpractice.domain.notification.entity.DeviceToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByUserIdAndDeviceToken(Long userId, String deviceToken);

    List<DeviceToken> findAllByUserId(Long userId);
}
