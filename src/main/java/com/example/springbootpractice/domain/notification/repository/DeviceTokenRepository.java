package com.example.springbootpractice.domain.notification.repository;

import com.example.springbootpractice.domain.notification.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
}
