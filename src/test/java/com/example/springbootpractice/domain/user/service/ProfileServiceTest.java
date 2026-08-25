package com.example.springbootpractice.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProfileServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Test
    void create_성공() {
        User user = userService.register("profile-test1@example.com", "password123", "01011110000");

        Profile profile = profileService.create(user.getId(), "닉네임1", "DEFAULT");

        assertNotNull(profile.getProfileId());
        assertEquals("닉네임1", profile.getNickname());
    }

    @Test
    void create_4번째_생성시_예외() {
        User user = userService.register("profile-test2@example.com", "password123", "01022220000");

        profileService.create(user.getId(), "닉네임1", "DEFAULT");
        profileService.create(user.getId(), "닉네임2", "OPEN_CHAT");
        profileService.create(user.getId(), "닉네임3", "BUSINESS");

        BusinessException e = assertThrows(BusinessException.class,
                () -> profileService.create(user.getId(), "닉네임4", "DEFAULT"));
        assertEquals(ErrorCode.PROFILE_LIMIT_EXCEEDED, e.getErrorCode());
    }

    @Test
    void findById_존재하지_않으면_예외() {
        BusinessException e = assertThrows(BusinessException.class, () -> profileService.findById(999_999L));
        assertEquals(ErrorCode.PROFILE_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void update_성공() {
        User user = userService.register("profile-test3@example.com", "password123", "01011113000");
        Profile profile = profileService.create(user.getId(), "원래닉네임", "DEFAULT");

        Profile updated = profileService.update(user.getId(), profile.getProfileId(), "새닉네임", "상태메시지", null);

        assertEquals("새닉네임", updated.getNickname());
        assertEquals("상태메시지", updated.getStatusMessage());
    }

    @Test
    void update_남의프로필이면_예외() {
        User owner = userService.register("profile-test4@example.com", "password123", "01011114000");
        User stranger = userService.register("profile-test5@example.com", "password123", "01011115000");
        Profile profile = profileService.create(owner.getId(), "닉네임", "DEFAULT");

        BusinessException e = assertThrows(BusinessException.class,
                () -> profileService.update(stranger.getId(), profile.getProfileId(), "해킹", null, null));
        assertEquals(ErrorCode.PROFILE_NOT_FOUND, e.getErrorCode());
    }
}
