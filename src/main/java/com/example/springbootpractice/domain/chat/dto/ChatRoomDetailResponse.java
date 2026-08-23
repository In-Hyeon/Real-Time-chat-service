package com.example.springbootpractice.domain.chat.dto;

import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import java.util.List;

public record ChatRoomDetailResponse(
        Long roomId,
        String roomName,
        String roomType,
        List<RoomParticipantResponse> participants
) {
    public static ChatRoomDetailResponse of(List<RoomParticipant> participants) {
        ChatRoom room = participants.get(0).getRoom();
        return new ChatRoomDetailResponse(
                room.getId(),
                room.getRoomName(),
                room.getRoomType(),
                participants.stream().map(RoomParticipantResponse::from).toList()
        );
    }
}
