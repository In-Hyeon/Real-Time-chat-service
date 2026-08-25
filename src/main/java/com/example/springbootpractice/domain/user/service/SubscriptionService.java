package com.example.springbootpractice.domain.user.service;

import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.entity.UserSubscription;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.domain.user.repository.UserSubscriptionRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserSubscription subscribe(Long userId, long days) {
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(days);

        return userSubscriptionRepository.findByUserId(userId)
                .map(subscription -> {
                    subscription.activate(expiredAt);
                    return subscription;
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    UserSubscription subscription = UserSubscription.create(user);
                    subscription.activate(expiredAt);
                    return userSubscriptionRepository.save(subscription);
                });
    }

    public UserSubscription findMy(Long userId) {
        return userSubscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
    }

    @Transactional
    public void cancel(Long userId) {
        UserSubscription subscription = userSubscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscription.deactivate();
    }
}
