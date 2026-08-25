package com.example.springbootpractice.domain.chat.controller;

import com.example.springbootpractice.domain.chat.dto.ChatRoomCreateRequest;
import com.example.springbootpractice.domain.chat.dto.ChatRoomDetailResponse;
import com.example.springbootpractice.domain.chat.dto.ChatRoomResponse;
import com.example.springbootpractice.domain.chat.dto.ChatRoomSettingsUpdateRequest;
import com.example.springbootpractice.domain.chat.dto.MarkAsReadRequest;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.chat.service.ChatRoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChatRoomResponse> create(@AuthenticationPrincipal Long userId,
                                                 @RequestBody ChatRoomCreateRequest request) {
        RoomParticipant myParticipant = chatRoomService.create(userId, request.roomName(), request.roomType(),
                request.myProfileId(), request.otherProfileIds());
        return ApiResponse.success(ChatRoomResponse.of(myParticipant, 0L));
    }

    @GetMapping
    public ApiResponse<List<ChatRoomResponse>> findMyRooms(@AuthenticationPrincipal Long userId) {
        List<ChatRoomResponse> rooms = chatRoomService.findMyRooms(userId).stream()
                .map(p -> ChatRoomResponse.of(p, chatRoomService.countUnread(p)))
                .toList();
        return ApiResponse.success(rooms);
    }

    @GetMapping("/{roomId}")
    public ApiResponse<ChatRoomDetailResponse> findRoom(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long roomId) {
        List<RoomParticipant> participants = chatRoomService.findParticipants(userId, roomId);
        return ApiResponse.success(ChatRoomDetailResponse.of(participants));
    }

    @PatchMapping("/{roomId}/settings")
    public ApiResponse<ChatRoomResponse> updateSettings(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long roomId,
                                                         @RequestBody ChatRoomSettingsUpdateRequest request) {
        RoomParticipant participant = chatRoomService.updateMySettings(userId, roomId, request.customRoomName(),
                request.isMuted(), request.isPinned(), request.backgroundImageUrl());
        return ApiResponse.success(ChatRoomResponse.of(participant, chatRoomService.countUnread(participant)));
    }

    @DeleteMapping("/{roomId}/leave")
    public ApiResponse<Void> leave(@AuthenticationPrincipal Long userId, @PathVariable Long roomId) {
        chatRoomService.leave(userId, roomId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{roomId}/read")
    public ApiResponse<Void> markAsRead(@AuthenticationPrincipal Long userId, @PathVariable Long roomId,
                                         @RequestBody MarkAsReadRequest request) {
        chatRoomService.markAsRead(userId, roomId, request.lastReadMessageId());
        return ApiResponse.success(null);
    }
}
