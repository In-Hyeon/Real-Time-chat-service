package com.example.springbootpractice.domain.emoticon.dto;

public record EmojiCreateRequest(
        String imageUrl,
        String category
) {
}
