package com.example.springbootpractice.domain.chat.dto;

public record ChatRoomSettingsUpdateRequest(
        String customRoomName,
        Boolean isMuted,
        Boolean isPinned,
        String backgroundImageUrl
) {
}
