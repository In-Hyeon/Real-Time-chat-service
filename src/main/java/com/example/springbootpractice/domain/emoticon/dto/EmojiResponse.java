package com.example.springbootpractice.domain.emoticon.dto;

import com.example.springbootpractice.domain.emoticon.entity.Emoji;

public record EmojiResponse(
        Long id,
        String imageUrl,
        String category
) {
    public static EmojiResponse from(Emoji emoji) {
        return new EmojiResponse(emoji.getId(), emoji.getImageUrl(), emoji.getCategory());
    }
}
