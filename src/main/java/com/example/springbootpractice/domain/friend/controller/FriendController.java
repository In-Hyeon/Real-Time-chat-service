package com.example.springbootpractice.domain.friend.controller;

import com.example.springbootpractice.domain.friend.dto.FriendAddRequest;
import com.example.springbootpractice.domain.friend.dto.FriendResponse;
import com.example.springbootpractice.domain.friend.dto.FriendUpdateRequest;
import com.example.springbootpractice.domain.friend.entity.Friend;
import com.example.springbootpractice.domain.friend.service.FriendService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FriendResponse> add(@AuthenticationPrincipal Long userId,
                                            @RequestBody FriendAddRequest request) {
        Friend friend = friendService.add(userId, request.friendUserId());
        return ApiResponse.success(FriendResponse.from(friend));
    }

    @GetMapping
    public ApiResponse<List<FriendResponse>> findMine(@AuthenticationPrincipal Long userId) {
        List<FriendResponse> friends = friendService.findAllByUser(userId).stream()
                .map(FriendResponse::from)
                .toList();
        return ApiResponse.success(friends);
    }

    @PatchMapping("/{id}")
    public ApiResponse<FriendResponse> update(@AuthenticationPrincipal Long userId,
                                               @PathVariable Long id,
                                               @RequestBody FriendUpdateRequest request) {
        Friend friend = friendService.update(userId, id, request.status(), request.alias());
        return ApiResponse.success(FriendResponse.from(friend));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        friendService.remove(userId, id);
        return ApiResponse.success(null);
    }
}
