package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.AuthSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByUserId(Long userId);

    Optional<AuthSession> findByRefreshToken(String refreshToken);
}
