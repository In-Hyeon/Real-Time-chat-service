package com.example.springbootpractice.domain.auth.service;

import com.example.springbootpractice.domain.auth.dto.LoginResponse;
import com.example.springbootpractice.domain.user.entity.AuthSession;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.AuthSessionRepository;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import com.example.springbootpractice.global.jwt.JwtProperties;
import com.example.springbootpractice.global.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(String email, String password, String deviceId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(user, deviceId);
    }

    @Transactional
    public LoginResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.isValid(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        AuthSession authSession = authSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (authSession.getExpiredAt().isBefore(LocalDateTime.now())) {
            authSessionRepository.delete(authSession);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        return issueTokens(authSession.getUser(), authSession.getDeviceId());
    }

    @Transactional
    public void logout(Long userId) {
        authSessionRepository.findByUserId(userId)
                .ifPresent(authSessionRepository::delete);
    }

    private LoginResponse issueTokens(User user, String deviceId) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // AUTH_SESSION.user_id가 unique 제약이라 사용자당 세션은 1개만 존재할 수 있음.
        // save()는 IDENTITY 전략상 즉시 insert되므로, 기존 행을 flush로 먼저 지워야 중복 키 충돌이 안 남.
        authSessionRepository.findByUserId(user.getId())
                .ifPresent(session -> {
                    authSessionRepository.delete(session);
                    authSessionRepository.flush();
                });

        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenValiditySeconds());
        authSessionRepository.save(AuthSession.create(user, deviceId, refreshToken, expiredAt));

        return new LoginResponse(accessToken, refreshToken);
    }
}
