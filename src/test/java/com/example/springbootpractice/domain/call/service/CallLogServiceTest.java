package com.example.springbootpractice.domain.call.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.springbootpractice.domain.call.entity.CallLog;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.chat.service.ChatRoomService;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.ProfileService;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CallLogServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private CallLogService callLogService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void create_성공() {
        User caller = userService.register("call-a@example.com", "password123", "01077770501");
        User receiver = userService.register("call-b@example.com", "password123", "01077770502");
        Profile callerProfile = profileService.create(caller.getId(), "caller", "DEFAULT");
        Profile receiverProfile = profileService.create(receiver.getId(), "receiver", "DEFAULT");
        Long callerId = caller.getId();
        Long receiverId = receiver.getId();
        entityManager.flush();
        entityManager.clear();

        RoomParticipant participant = chatRoomService.create(
                callerId, "통화방", "DIRECT", callerProfile.getProfileId(), List.of(receiverProfile.getProfileId()));
        Long roomId = participant.getRoom().getId();

        CallLog callLog = callLogService.create(callerId, roomId, receiverId, "VOICE", "CONNECTED", 120);

        assertEquals(120, callLog.getDurationSeconds());
        assertEquals(1, callLogService.findMyCallLogs(callerId).size());
    }

    @Test
    void create_참여자아니면_예외() {
        User caller = userService.register("call-c@example.com", "password123", "01077770503");
        User other = userService.register("call-d@example.com", "password123", "01077770504");
        User stranger = userService.register("call-e@example.com", "password123", "01077770505");
        Profile callerProfile = profileService.create(caller.getId(), "caller2", "DEFAULT");
        Long callerId = caller.getId();
        Long strangerId = stranger.getId();
        Long otherId = other.getId();
        entityManager.flush();
        entityManager.clear();

        RoomParticipant participant =
                chatRoomService.create(callerId, "통화방2", "DIRECT", callerProfile.getProfileId(), List.of());
        Long roomId = participant.getRoom().getId();

        BusinessException e = assertThrows(BusinessException.class,
                () -> callLogService.create(strangerId, roomId, otherId, "VOICE", "CONNECTED", 0));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, e.getErrorCode());
    }
}
