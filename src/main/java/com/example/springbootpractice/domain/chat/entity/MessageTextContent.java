package com.example.springbootpractice.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MESSAGE_TEXT_CONTENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageTextContent {

    // @GeneratedValue가 없다: 이 PK는 DB가 새로 채번하는 게 아니라
    // 아래 message 연관관계의 PK 값을 그대로 물려받는다 (@MapsId).
    @Id
    @Column(name = "message_id")
    private Long messageId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "message_id")
    private Message message;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public static MessageTextContent create(Message message, String content) {
        MessageTextContent textContent = new MessageTextContent();
        textContent.message = message;
        textContent.content = content;
        return textContent;
    }
}
