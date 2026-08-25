package com.example.springbootpractice.domain.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springbootpractice.domain.chat.dto.MessageResponse;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.emoticon.service.EmojiService;
import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import com.example.springbootpractice.domain.gift.service.GiftService;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.ProfileService;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EmojiService emojiService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private EntityManager entityManager;

    private record TestUser(Long userId, Long profileId) {
    }

    private TestUser registerUserWithProfile(String email, String phone, String nickname) {
        User user = userService.register(email, "password123", phone);
        Profile profile = profileService.create(user.getId(), nickname, "DEFAULT");
        Long userId = user.getId();
        Long profileId = profile.getProfileId();
        entityManager.flush();
        entityManager.clear();
        return new TestUser(userId, profileId);
    }

    private Long createRoom(TestUser owner, TestUser... others) {
        List<Long> otherIds = List.of(others).stream().map(TestUser::profileId).toList();
        RoomParticipant participant =
                chatRoomService.create(owner.userId(), "테스트방", "GROUP", owner.profileId(), otherIds);
        Long roomId = participant.getRoom().getId();
        entityManager.flush();
        entityManager.clear();
        return roomId;
    }

    @Test
    void send_텍스트메시지_성공() {
        TestUser me = registerUserWithProfile("msg-a@example.com", "01088880001", "나");
        Long roomId = createRoom(me);

        MessageResponse response = messageService.send(me.userId(), roomId, "TEXT", "안녕", null, null, null, null, null, null);

        assertNotNull(response.messageId());
        assertEquals("안녕", response.content());
        assertEquals("TEXT", response.messageType());
    }

    @Test
    void send_참여자아니면_예외() {
        TestUser me = registerUserWithProfile("msg-b@example.com", "01088880002", "나");
        TestUser stranger = registerUserWithProfile("msg-c@example.com", "01088880003", "타인");
        Long roomId = createRoom(me);

        BusinessException e = assertThrows(BusinessException.class,
                () -> messageService.send(stranger.userId(), roomId, "TEXT", "몰래", null, null, null, null, null, null));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void send_내용없는텍스트면_예외() {
        TestUser me = registerUserWithProfile("msg-d@example.com", "01088880004", "나");
        Long roomId = createRoom(me);

        BusinessException e = assertThrows(BusinessException.class,
                () -> messageService.send(me.userId(), roomId, "TEXT", "  ", null, null, null, null, null, null));
        assertEquals(ErrorCode.INVALID_MESSAGE_CONTENT, e.getErrorCode());
    }

    @Test
    void findMessages_목록조회_성공() {
        TestUser me = registerUserWithProfile("msg-e@example.com", "01088880005", "나");
        Long roomId = createRoom(me);

        messageService.send(me.userId(), roomId, "TEXT", "첫번째", null, null, null, null, null, null);
        messageService.send(me.userId(), roomId, "TEXT", "두번째", null, null, null, null, null, null);

        List<MessageResponse> messages = messageService.findMessages(me.userId(), roomId, null, 30);

        assertEquals(2, messages.size());
        assertEquals("두번째", messages.get(0).content());
        assertEquals("첫번째", messages.get(1).content());
    }

    @Test
    void delete_본인메시지_성공() {
        TestUser me = registerUserWithProfile("msg-f@example.com", "01088880006", "나");
        Long roomId = createRoom(me);
        MessageResponse sent = messageService.send(me.userId(), roomId, "TEXT", "지울거야", null, null, null, null, null, null);

        messageService.delete(me.userId(), roomId, sent.messageId());

        List<MessageResponse> messages = messageService.findMessages(me.userId(), roomId, null, 30);
        assertTrue(messages.get(0).deleted());
        assertNull(messages.get(0).content());
    }

    @Test
    void delete_남의메시지면_예외() {
        TestUser me = registerUserWithProfile("msg-g@example.com", "01088880007", "나");
        TestUser other = registerUserWithProfile("msg-h@example.com", "01088880008", "상대");
        Long roomId = createRoom(me, other);
        MessageResponse sent = messageService.send(me.userId(), roomId, "TEXT", "내꺼", null, null, null, null, null, null);

        BusinessException e = assertThrows(BusinessException.class,
                () -> messageService.delete(other.userId(), roomId, sent.messageId()));
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 답장_부모메시지연결_성공() {
        TestUser me = registerUserWithProfile("msg-i@example.com", "01088880009", "나");
        Long roomId = createRoom(me);
        MessageResponse parent = messageService.send(me.userId(), roomId, "TEXT", "원본", null, null, null, null, null, null);

        MessageResponse reply = messageService.send(
                me.userId(), roomId, "TEXT", "답장", null, null, null, parent.messageId(), null, null);

        assertEquals(parent.messageId(), reply.parentMessageId());
    }

    @Test
    void 읽음처리와_안읽은개수_연동() {
        TestUser me = registerUserWithProfile("msg-j@example.com", "01088880010", "나");
        TestUser other = registerUserWithProfile("msg-k@example.com", "01088880011", "상대");
        Long roomId = createRoom(me, other);

        messageService.send(me.userId(), roomId, "TEXT", "1", null, null, null, null, null, null);
        MessageResponse second = messageService.send(me.userId(), roomId, "TEXT", "2", null, null, null, null, null, null);
        messageService.send(me.userId(), roomId, "TEXT", "3", null, null, null, null, null, null);

        chatRoomService.markAsRead(other.userId(), roomId, second.messageId());

        RoomParticipant otherParticipant = chatRoomService.findMyRooms(other.userId()).get(0);
        assertEquals(1, chatRoomService.countUnread(otherParticipant));
    }

    @Test
    void send_이모티콘메시지_보유한경우_성공() {
        TestUser me = registerUserWithProfile("msg-l@example.com", "01088880012", "나");
        Long roomId = createRoom(me);
        Emoji emoji = emojiService.create("http://example.com/e.png", "웃음");
        emojiService.acquire(me.userId(), emoji.getId());

        MessageResponse response =
                messageService.send(me.userId(), roomId, "EMOJI", null, null, null, null, null, emoji.getId(), null);

        assertEquals("EMOJI", response.messageType());
        assertEquals(emoji.getId(), response.emojiId());
    }

    @Test
    void send_이모티콘메시지_보유안했고구독도아니면_예외() {
        TestUser me = registerUserWithProfile("msg-m@example.com", "01088880013", "나");
        Long roomId = createRoom(me);
        Emoji emoji = emojiService.create("http://example.com/f.png", "슬픔");

        BusinessException e = assertThrows(BusinessException.class, () -> messageService.send(
                me.userId(), roomId, "EMOJI", null, null, null, null, null, emoji.getId(), null));
        assertEquals(ErrorCode.EMOJI_NOT_USABLE, e.getErrorCode());
    }

    @Test
    void send_선물메시지_성공() {
        TestUser me = registerUserWithProfile("msg-n@example.com", "01088880014", "나");
        TestUser other = registerUserWithProfile("msg-o@example.com", "01088880015", "상대");
        Long roomId = createRoom(me, other);
        GiftProduct product = giftService.createProduct("커피", "이디야", new BigDecimal("3500"));
        Voucher voucher = giftService.order(me.userId(), other.userId(), product.getId());

        MessageResponse response = messageService.send(
                me.userId(), roomId, "GIFT", null, null, null, null, null, null, voucher.getId());

        assertEquals("GIFT", response.messageType());
        assertEquals(voucher.getId(), response.voucherId());
        assertEquals("커피", response.giftProductName());
    }
}
