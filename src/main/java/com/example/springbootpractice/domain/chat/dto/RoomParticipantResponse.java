package com.example.springbootpractice.domain.chat.dto;

import com.example.springbootpractice.domain.chat.entity.RoomParticipant;

public record RoomParticipantResponse(
        Long profileId,
        String nickname
) {
    public static RoomParticipantResponse from(RoomParticipant participant) {
        return new RoomParticipantResponse(
                participant.getProfile().getProfileId(),
                participant.getProfile().getNickname()
        );
    }
}
