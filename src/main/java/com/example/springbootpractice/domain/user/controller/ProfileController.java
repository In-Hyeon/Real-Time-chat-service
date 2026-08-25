package com.example.springbootpractice.domain.user.controller;

import com.example.springbootpractice.domain.user.dto.ProfileCreateRequest;
import com.example.springbootpractice.domain.user.dto.ProfileResponse;
import com.example.springbootpractice.domain.user.dto.ProfileUpdateRequest;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.service.ProfileService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProfileResponse> create(@AuthenticationPrincipal Long userId,
                                                @RequestBody ProfileCreateRequest request) {
        Profile profile = profileService.create(userId, request.nickname(), request.profileType());
        return ApiResponse.success(ProfileResponse.from(profile));
    }

    @GetMapping
    public ApiResponse<List<ProfileResponse>> findMine(@AuthenticationPrincipal Long userId) {
        List<ProfileResponse> profiles = profileService.findAllByUser(userId).stream()
                .map(ProfileResponse::from)
                .toList();
        return ApiResponse.success(profiles);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> findById(@PathVariable Long id) {
        Profile profile = profileService.findById(id);
        return ApiResponse.success(ProfileResponse.from(profile));
    }

    @PatchMapping("/{id}")
    public ApiResponse<ProfileResponse> update(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long id,
                                                @RequestBody ProfileUpdateRequest request) {
        Profile profile = profileService.update(userId, id, request.nickname(), request.statusMessage(),
                request.profileImageUrl());
        return ApiResponse.success(ProfileResponse.from(profile));
    }
}
