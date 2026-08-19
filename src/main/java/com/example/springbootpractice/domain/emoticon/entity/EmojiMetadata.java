package com.example.springbootpractice.domain.emoticon.entity;

import com.example.springbootpractice.domain.chat.entity.Message;
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
@Table(name = "EMOJI_METADATA")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmojiMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emoji_id", nullable = false)
    private Emoji emoji;

    public static EmojiMetadata create(Message message, Emoji emoji) {
        EmojiMetadata metadata = new EmojiMetadata();
        metadata.message = message;
        metadata.emoji = emoji;
        return metadata;
    }
}
