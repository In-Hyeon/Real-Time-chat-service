package com.example.springbootpractice.domain.chat.dto;

import java.util.List;

public record ChatRoomCreateRequest(
        String roomName,
        String roomType,
        Long myProfileId,
        List<Long> otherProfileIds
) {
}
