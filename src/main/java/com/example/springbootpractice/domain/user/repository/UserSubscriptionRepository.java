package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.UserSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserId(Long userId);
}
