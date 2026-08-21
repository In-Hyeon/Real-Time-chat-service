package com.example.springbootpractice.domain.user.service;

import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String email, String password, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(email, encodedPassword, phoneNumber);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
