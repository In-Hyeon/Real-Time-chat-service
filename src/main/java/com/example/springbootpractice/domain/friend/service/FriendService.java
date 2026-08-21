package com.example.springbootpractice.domain.friend.service;

import com.example.springbootpractice.domain.friend.entity.Friend;
import com.example.springbootpractice.domain.friend.repository.FriendRepository;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "HIDDEN", "BLOCKED", "FAVORITE");

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public Friend add(Long userId, Long friendUserId) {
        if (userId.equals(friendUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_FRIEND_SELF);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User friendUser = userRepository.findById(friendUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (friendRepository.existsByUserIdAndFriendId(userId, friendUserId)) {
            throw new BusinessException(ErrorCode.ALREADY_FRIEND);
        }

        return friendRepository.save(Friend.create(user, friendUser));
    }

    public List<Friend> findAllByUser(Long userId) {
        return friendRepository.findAllByUserId(userId);
    }

    @Transactional
    public Friend update(Long userId, Long friendRelationId, String status, String alias) {
        Friend friend = findOwned(userId, friendRelationId);

        if (status != null) {
            if (!VALID_STATUSES.contains(status)) {
                throw new BusinessException(ErrorCode.INVALID_FRIEND_STATUS);
            }
            friend.updateStatus(status);
        }
        if (alias != null) {
            friend.updateAlias(alias);
        }

        return friend;
    }

    @Transactional
    public void remove(Long userId, Long friendRelationId) {
        friendRepository.delete(findOwned(userId, friendRelationId));
    }

    private Friend findOwned(Long userId, Long friendRelationId) {
        Friend friend = friendRepository.findById(friendRelationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_NOT_FOUND));

        // 다른 사람의 친구 관계 id를 넣었을 때 "존재는 하지만 권한 없음"이 아니라
        // "존재하지 않음"으로 응답해 남의 관계 id 존재 여부 자체가 새어나가지 않게 함
        if (!friend.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FRIEND_NOT_FOUND);
        }

        return friend;
    }
}
