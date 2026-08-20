package com.example.springbootpractice.domain.user.dto;

import com.example.springbootpractice.domain.user.entity.User;

public record UserResponse(
        Long id,
        String email,
        String phoneNumber
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getPhoneNumber());
    }
}
