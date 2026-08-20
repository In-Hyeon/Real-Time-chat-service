package com.example.springbootpractice.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void register_성공() {
        User user = userService.register("service-test@example.com", "password123", "01011112222");

        assertNotNull(user.getId());
        assertEquals("service-test@example.com", user.getEmail());
    }

    @Test
    void register_이메일_중복이면_예외() {
        userService.register("dup@example.com", "password123", "01033334444");

        BusinessException e = assertThrows(BusinessException.class,
                () -> userService.register("dup@example.com", "password456", "01055556666"));
        assertEquals(ErrorCode.DUPLICATE_EMAIL, e.getErrorCode());
    }

    @Test
    void findById_존재하지_않으면_예외() {
        BusinessException e = assertThrows(BusinessException.class, () -> userService.findById(999_999L));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}
