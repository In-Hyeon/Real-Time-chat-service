package com.example.springbootpractice.domain.user.dto;

import com.example.springbootpractice.domain.user.entity.Profile;

public record ProfileResponse(
        Long profileId,
        String nickname,
        String statusMessage,
        String profileImageUrl,
        String profileType
) {
    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getProfileId(),
                profile.getNickname(),
                profile.getStatusMessage(),
                profile.getProfileImageUrl(),
                profile.getProfileType()
        );
    }
}
