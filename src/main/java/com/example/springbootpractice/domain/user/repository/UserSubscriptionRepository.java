package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
}
