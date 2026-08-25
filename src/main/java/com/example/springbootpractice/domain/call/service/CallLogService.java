package com.example.springbootpractice.domain.call.service;

import com.example.springbootpractice.domain.call.entity.CallLog;
import com.example.springbootpractice.domain.call.repository.CallLogRepository;
import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.chat.repository.ChatRoomRepository;
import com.example.springbootpractice.domain.chat.repository.RoomParticipantRepository;
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
public class CallLogService {

    private static final Set<String> VALID_CALL_TYPES = Set.of("VOICE", "FACE");
    private static final Set<String> VALID_CALL_STATUSES = Set.of("CONNECTED", "REJECTED", "MISSED", "BUSY");

    private final CallLogRepository callLogRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserRepository userRepository;

    @Transactional
    public CallLog create(Long callerId, Long roomId, Long receiverUserId, String callType, String callStatus,
                           int durationSeconds) {
        if (!VALID_CALL_TYPES.contains(callType)) {
            throw new BusinessException(ErrorCode.INVALID_CALL_TYPE);
        }
        if (!VALID_CALL_STATUSES.contains(callStatus)) {
            throw new BusinessException(ErrorCode.INVALID_CALL_STATUS);
        }

        roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, callerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return callLogRepository.save(
                CallLog.create(room, caller, receiver, callType, callStatus, durationSeconds));
    }

    public List<CallLog> findMyCallLogs(Long userId) {
        return callLogRepository.findAllByUserId(userId);
    }
}
