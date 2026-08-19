package com.example.springbootpractice.domain.emoticon.entity;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_EMOJI")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmoji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emoji_id", nullable = false)
    private Emoji emoji;

    public static UserEmoji create(User user, Emoji emoji) {
        UserEmoji userEmoji = new UserEmoji();
        userEmoji.user = user;
        userEmoji.emoji = emoji;
        return userEmoji;
    }
}
