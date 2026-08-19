package com.example.springbootpractice.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "AUTH_SESSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @Column(name = "refresh_token", nullable = false, length = 500)
    private String refreshToken;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public static AuthSession create(User user, String deviceId, String refreshToken, LocalDateTime expiredAt) {
        AuthSession authSession = new AuthSession();
        authSession.user = user;
        authSession.deviceId = deviceId;
        authSession.refreshToken = refreshToken;
        authSession.issuedAt = LocalDateTime.now();
        authSession.expiredAt = expiredAt;
        return authSession;
    }
}
