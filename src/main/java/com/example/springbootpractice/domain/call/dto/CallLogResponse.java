package com.example.springbootpractice.domain.call.dto;

import com.example.springbootpractice.domain.call.entity.CallLog;
import java.time.LocalDateTime;

public record CallLogResponse(
        Long id,
        Long roomId,
        Long callerId,
        Long receiverId,
        String callType,
        String callStatus,
        int durationSeconds,
        LocalDateTime createdAt
) {
    public static CallLogResponse from(CallLog callLog) {
        return new CallLogResponse(
                callLog.getId(),
                callLog.getRoom().getId(),
                callLog.getCaller().getId(),
                callLog.getReceiver().getId(),
                callLog.getCallType(),
                callLog.getCallStatus(),
                callLog.getDurationSeconds(),
                callLog.getCreatedAt()
        );
    }
}
