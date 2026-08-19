package com.example.springbootpractice.domain.chat.entity;

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
@Table(name = "CHAT_ROOM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(name = "room_type", nullable = false, length = 20)
    private String roomType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ChatRoom create(String roomName, String roomType) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomName = roomName;
        chatRoom.roomType = roomType;
        chatRoom.createdAt = LocalDateTime.now();
        return chatRoom;
    }
}
