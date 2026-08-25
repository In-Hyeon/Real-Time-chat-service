package com.example.springbootpractice.domain.chat.dto;

import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;

public record ChatRoomResponse(
        Long roomId,
        String roomName,
        String roomType,
        String myCustomRoomName,
        boolean isMuted,
        boolean isPinned,
        String backgroundImageUrl,
        long unreadCount
) {
    public static ChatRoomResponse of(RoomParticipant participant, long unreadCount) {
        ChatRoom room = participant.getRoom();
        return new ChatRoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getRoomType(),
                participant.getCustomRoomName(),
                participant.isMuted(),
                participant.isPinned(),
                participant.getBackgroundImageUrl(),
                unreadCount
        );
    }
}
