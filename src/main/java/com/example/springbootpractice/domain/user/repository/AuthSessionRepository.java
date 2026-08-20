package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {
}
