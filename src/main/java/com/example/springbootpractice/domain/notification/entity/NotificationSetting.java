package com.example.springbootpractice.domain.notification.entity;

import com.example.springbootpractice.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NOTIFICATION_SETTING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @Column(name = "is_sound", nullable = false)
    private boolean isSound = true;

    @Column(name = "quiet_start_time")
    private LocalTime quietStartTime;

    @Column(name = "quiet_end_time")
    private LocalTime quietEndTime;

    public static NotificationSetting create(User user) {
        NotificationSetting setting = new NotificationSetting();
        setting.user = user;
        return setting;
    }
}
