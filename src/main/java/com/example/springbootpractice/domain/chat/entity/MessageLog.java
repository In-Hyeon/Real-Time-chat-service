package com.example.springbootpractice.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MESSAGE_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    // DDL에 FK 제약이 없는 감사 로그 테이블이라 연관관계 대신 순수 값으로 매핑
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "action_type", nullable = false, length = 255)
    private String actionType;

    @Lob
    @Column(name = "raw_content", nullable = false, columnDefinition = "TEXT")
    private String rawContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static MessageLog create(Long messageId, Long roomId, Long senderId, String actionType, String rawContent) {
        MessageLog log = new MessageLog();
        log.messageId = messageId;
        log.roomId = roomId;
        log.senderId = senderId;
        log.actionType = actionType;
        log.rawContent = rawContent;
        log.createdAt = LocalDateTime.now();
        return log;
    }
}
