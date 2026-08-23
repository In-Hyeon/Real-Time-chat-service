package com.example.springbootpractice.domain.chat.service;

import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.chat.repository.ChatRoomRepository;
import com.example.springbootpractice.domain.chat.repository.RoomParticipantRepository;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.repository.ProfileRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private static final Set<String> VALID_ROOM_TYPES = Set.of("DIRECT", "GROUP", "OPEN_DIRECT", "OPEN_GROUP", "MY");
    private static final long INITIAL_LAST_READ_MESSAGE_ID = 0L;

    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public RoomParticipant create(Long userId, String roomName, String roomType, Long myProfileId,
                                   List<Long> otherProfileIds) {
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new BusinessException(ErrorCode.INVALID_ROOM_TYPE);
        }

        Profile myProfile = profileRepository.findById(myProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        if (!myProfile.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        ChatRoom room = chatRoomRepository.save(ChatRoom.create(roomName, roomType));
        RoomParticipant myParticipant =
                roomParticipantRepository.save(RoomParticipant.create(room, myProfile, INITIAL_LAST_READ_MESSAGE_ID));

        Set<Long> uniqueOtherProfileIds = otherProfileIds == null ? Set.of() : new LinkedHashSet<>(otherProfileIds);
        uniqueOtherProfileIds.remove(myProfileId);

        for (Long profileId : uniqueOtherProfileIds) {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
            roomParticipantRepository.save(RoomParticipant.create(room, profile, INITIAL_LAST_READ_MESSAGE_ID));
        }

        return myParticipant;
    }

    public List<RoomParticipant> findMyRooms(Long userId) {
        return roomParticipantRepository.findAllByProfileUserId(userId);
    }

    public List<RoomParticipant> findParticipants(Long userId, Long roomId) {
        roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        return roomParticipantRepository.findAllByRoomId(roomId);
    }

    @Transactional
    public RoomParticipant updateMySettings(Long userId, Long roomId, String customRoomName, Boolean isMuted,
                                             Boolean isPinned, String backgroundImageUrl) {
        RoomParticipant participant = roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        participant.updateSettings(customRoomName, isMuted, isPinned, backgroundImageUrl);
        return participant;
    }

    @Transactional
    public void leave(Long userId, Long roomId) {
        RoomParticipant participant = roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        roomParticipantRepository.delete(participant);
    }
}
