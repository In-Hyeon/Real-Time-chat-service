package com.example.springbootpractice.domain.notification.entity;

import com.example.springbootpractice.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DEVICE_TOKEN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_token", nullable = false, length = 255)
    private String deviceToken;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static DeviceToken create(User user, String deviceToken, String deviceType) {
        DeviceToken token = new DeviceToken();
        token.user = user;
        token.deviceToken = deviceToken;
        token.deviceType = deviceType;
        token.updatedAt = LocalDateTime.now();
        return token;
    }
}
