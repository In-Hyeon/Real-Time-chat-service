package com.example.springbootpractice.domain.emoticon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMOJI")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emoji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Emoji create(String imageUrl, String category) {
        Emoji emoji = new Emoji();
        emoji.imageUrl = imageUrl;
        emoji.category = category;
        emoji.createdAt = LocalDateTime.now();
        return emoji;
    }
}
