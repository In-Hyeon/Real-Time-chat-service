package com.example.springbootpractice.domain.chat.dto;

import com.example.springbootpractice.domain.chat.entity.FileMetadata;
import com.example.springbootpractice.domain.chat.entity.Message;
import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import java.time.LocalDateTime;

public record MessageResponse(
        Long messageId,
        Long senderId,
        String messageType,
        String content,
        String fileUrl,
        String originalName,
        Long fileSize,
        Long parentMessageId,
        Long emojiId,
        String emojiImageUrl,
        Long voucherId,
        String giftProductName,
        boolean deleted,
        LocalDateTime createdAt
) {
    public static MessageResponse of(Message message, String content, FileMetadata fileMetadata, Emoji emoji,
                                      Voucher voucher) {
        boolean deleted = message.isDeleted();
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getMessageType(),
                deleted ? null : content,
                deleted || fileMetadata == null ? null : fileMetadata.getFileUrl(),
                deleted || fileMetadata == null ? null : fileMetadata.getOriginalName(),
                deleted || fileMetadata == null ? null : fileMetadata.getFileSize(),
                message.getParentMessageId(),
                deleted || emoji == null ? null : emoji.getId(),
                deleted || emoji == null ? null : emoji.getImageUrl(),
                deleted || voucher == null ? null : voucher.getId(),
                deleted || voucher == null ? null : voucher.getOrder().getProduct().getProductName(),
                deleted,
                message.getCreatedAt()
        );
    }
}
