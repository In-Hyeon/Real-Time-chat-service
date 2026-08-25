package com.example.springbootpractice.domain.call.dto;

public record CallLogCreateRequest(
        Long roomId,
        Long receiverUserId,
        String callType,
        String callStatus,
        int durationSeconds
) {
}
