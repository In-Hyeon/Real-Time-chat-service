package com.example.springbootpractice.domain.friend.dto;

import com.example.springbootpractice.domain.friend.entity.Friend;

public record FriendResponse(
        Long id,
        Long friendUserId,
        String friendEmail,
        String alias,
        String status
) {
    public static FriendResponse from(Friend friend) {
        return new FriendResponse(
                friend.getId(),
                friend.getFriend().getId(),
                friend.getFriend().getEmail(),
                friend.getAlias(),
                friend.getStatus()
        );
    }
}
