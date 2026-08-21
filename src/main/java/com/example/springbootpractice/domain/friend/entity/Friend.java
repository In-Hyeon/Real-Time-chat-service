package com.example.springbootpractice.domain.friend.entity;

import com.example.springbootpractice.domain.user.entity.Profile;
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
@Table(name = "FRIEND")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "alias", length = 50)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_profile_id")
    private Profile targetProfile;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Friend create(User user, User friend) {
        Friend relation = new Friend();
        relation.user = user;
        relation.friend = friend;
        relation.createdAt = LocalDateTime.now();
        return relation;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateAlias(String alias) {
        this.alias = alias;
    }
}
