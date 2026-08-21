package com.example.springbootpractice.domain.friend.dto;

public record FriendUpdateRequest(
        String status,
        String alias
) {
}
