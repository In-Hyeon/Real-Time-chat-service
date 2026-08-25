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
@Table(name = "USER_SUBSCRIPTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "is_emoticon_plus_active", nullable = false)
    private boolean isEmoticonPlusActive = false;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    public static UserSubscription create(User user) {
        UserSubscription subscription = new UserSubscription();
        subscription.user = user;
        return subscription;
    }

    public void activate(LocalDateTime expiredAt) {
        this.isEmoticonPlusActive = true;
        this.expiredAt = expiredAt;
    }

    public void deactivate() {
        this.isEmoticonPlusActive = false;
    }

    public boolean isCurrentlyActive() {
        return isEmoticonPlusActive && (expiredAt == null || expiredAt.isAfter(LocalDateTime.now()));
    }
}
