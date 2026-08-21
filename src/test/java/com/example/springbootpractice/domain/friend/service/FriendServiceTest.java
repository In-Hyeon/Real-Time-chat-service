package com.example.springbootpractice.domain.friend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.springbootpractice.domain.friend.entity.Friend;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FriendServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Test
    void add_성공() {
        User me = userService.register("friend-a@example.com", "password123", "01033330001");
        User other = userService.register("friend-b@example.com", "password123", "01033330002");

        Friend friend = friendService.add(me.getId(), other.getId());

        assertNotNull(friend.getId());
        assertEquals("ACTIVE", friend.getStatus());
    }

    @Test
    void add_자기자신이면_예외() {
        User me = userService.register("friend-c@example.com", "password123", "01033330003");

        BusinessException e = assertThrows(BusinessException.class,
                () -> friendService.add(me.getId(), me.getId()));
        assertEquals(ErrorCode.CANNOT_FRIEND_SELF, e.getErrorCode());
    }

    @Test
    void add_중복이면_예외() {
        User me = userService.register("friend-d@example.com", "password123", "01033330004");
        User other = userService.register("friend-e@example.com", "password123", "01033330005");

        friendService.add(me.getId(), other.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> friendService.add(me.getId(), other.getId()));
        assertEquals(ErrorCode.ALREADY_FRIEND, e.getErrorCode());
    }

    @Test
    void update_상태변경_성공() {
        User me = userService.register("friend-f@example.com", "password123", "01033330006");
        User other = userService.register("friend-g@example.com", "password123", "01033330007");
        Friend friend = friendService.add(me.getId(), other.getId());

        Friend updated = friendService.update(me.getId(), friend.getId(), "FAVORITE", "별칭");

        assertEquals("FAVORITE", updated.getStatus());
        assertEquals("별칭", updated.getAlias());
    }

    @Test
    void update_잘못된상태값이면_예외() {
        User me = userService.register("friend-h@example.com", "password123", "01033330008");
        User other = userService.register("friend-i@example.com", "password123", "01033330009");
        Friend friend = friendService.add(me.getId(), other.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> friendService.update(me.getId(), friend.getId(), "UNKNOWN", null));
        assertEquals(ErrorCode.INVALID_FRIEND_STATUS, e.getErrorCode());
    }

    @Test
    void update_남의관계면_존재하지않음_예외() {
        User me = userService.register("friend-j@example.com", "password123", "01033330010");
        User other = userService.register("friend-k@example.com", "password123", "01033330011");
        User stranger = userService.register("friend-l@example.com", "password123", "01033330012");
        Friend friend = friendService.add(me.getId(), other.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> friendService.update(stranger.getId(), friend.getId(), "FAVORITE", null));
        assertEquals(ErrorCode.FRIEND_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void remove_성공() {
        User me = userService.register("friend-m@example.com", "password123", "01033330013");
        User other = userService.register("friend-n@example.com", "password123", "01033330014");
        Friend friend = friendService.add(me.getId(), other.getId());

        friendService.remove(me.getId(), friend.getId());

        assertEquals(0, friendService.findAllByUser(me.getId()).size());
    }
}
