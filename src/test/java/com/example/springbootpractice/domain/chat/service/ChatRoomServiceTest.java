package com.example.springbootpractice.domain.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
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
class ChatRoomServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private EntityManager entityManager;

    private record TestUser(Long userId, Long profileId) {
    }

    /**
     * Profile.userId는 insertable=false/updatable=false로 매핑된 컬럼이라 방금 생성한 객체엔
     * 채워지지 않는다. ChatRoomService가 이 값을 참조하는 연관관계(user_id 조인)를 쓰기 때문에,
     * 여기서 flush+clear로 1차 캐시를 비워 이후 조회가 DB에서 다시 읽어오도록 강제한다.
     */
    private TestUser registerUserWithProfile(String email, String phone, String nickname) {
        User user = userService.register(email, "password123", phone);
        Profile profile = profileService.create(user.getId(), nickname, "DEFAULT");
        Long userId = user.getId();
        Long profileId = profile.getProfileId();
        entityManager.flush();
        entityManager.clear();
        return new TestUser(userId, profileId);
    }

    @Test
    void create_성공() {
        TestUser me = registerUserWithProfile("room-a@example.com", "01096660001", "나");
        TestUser other = registerUserWithProfile("room-b@example.com", "01096660002", "상대");

        RoomParticipant myParticipant = chatRoomService.create(
                me.userId(), "테스트방", "GROUP", me.profileId(), List.of(other.profileId()));

        assertNotNull(myParticipant.getId());
        List<RoomParticipant> participants =
                chatRoomService.findParticipants(me.userId(), myParticipant.getRoom().getId());
        assertEquals(2, participants.size());
    }

    @Test
    void create_존재하지않는프로필이면_예외() {
        TestUser me = registerUserWithProfile("room-c@example.com", "01066660003", "나");

        BusinessException e = assertThrows(BusinessException.class,
                () -> chatRoomService.create(me.userId(), "테스트방", "GROUP", me.profileId(), List.of(999_999L)));
        assertEquals(ErrorCode.PROFILE_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void create_잘못된타입이면_예외() {
        TestUser me = registerUserWithProfile("room-d@example.com", "01066660004", "나");

        BusinessException e = assertThrows(BusinessException.class,
                () -> chatRoomService.create(me.userId(), "테스트방", "WEIRD", me.profileId(), List.of()));
        assertEquals(ErrorCode.INVALID_ROOM_TYPE, e.getErrorCode());
    }

    @Test
    void findParticipants_참여자아니면_예외() {
        TestUser me = registerUserWithProfile("room-e@example.com", "01066660005", "나");
        TestUser stranger = registerUserWithProfile("room-f@example.com", "01066660006", "타인");

        RoomParticipant myParticipant = chatRoomService.create(me.userId(), "혼자방", "MY", me.profileId(), List.of());

        BusinessException e = assertThrows(BusinessException.class,
                () -> chatRoomService.findParticipants(stranger.userId(), myParticipant.getRoom().getId()));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void updateMySettings_성공() {
        TestUser me = registerUserWithProfile("room-g@example.com", "01066660007", "나");
        RoomParticipant myParticipant = chatRoomService.create(me.userId(), "혼자방", "MY", me.profileId(), List.of());

        RoomParticipant updated = chatRoomService.updateMySettings(
                me.userId(), myParticipant.getRoom().getId(), "커스텀이름", true, true, null);

        assertEquals("커스텀이름", updated.getCustomRoomName());
        assertTrue(updated.isMuted());
        assertTrue(updated.isPinned());
    }

    @Test
    void leave_성공() {
        TestUser me = registerUserWithProfile("room-h@example.com", "01066660008", "나");
        RoomParticipant myParticipant = chatRoomService.create(me.userId(), "혼자방", "MY", me.profileId(), List.of());

        chatRoomService.leave(me.userId(), myParticipant.getRoom().getId());

        assertEquals(0, chatRoomService.findMyRooms(me.userId()).size());
    }
}
