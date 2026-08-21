package com.example.springbootpractice.domain.user.service;

import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.ProfileRepository;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private static final int MAX_PROFILE_COUNT = 3;

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public Profile create(Long userId, String nickname, String profileType) {
        if (profileRepository.countByUserId(userId) >= MAX_PROFILE_COUNT) {
            throw new BusinessException(ErrorCode.PROFILE_LIMIT_EXCEEDED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Profile profile = Profile.create(user, nickname, profileType);
        return profileRepository.save(profile);
    }

    public List<Profile> findAllByUser(Long userId) {
        return profileRepository.findAllByUserId(userId);
    }

    public Profile findById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
    }
}
