package com.example.springbootpractice.domain.chat.dto;

public record MessageCreateRequest(
        String messageType,
        String content,
        String fileUrl,
        String originalName,
        Long fileSize,
        Long parentMessageId,
        Long emojiId,
        Long voucherId
) {
}
