package com.example.springbootpractice.domain.chat.service;

import com.example.springbootpractice.domain.chat.dto.MessageResponse;
import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.chat.entity.FileMetadata;
import com.example.springbootpractice.domain.chat.entity.Message;
import com.example.springbootpractice.domain.chat.entity.MessageLog;
import com.example.springbootpractice.domain.chat.entity.MessageTextContent;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.chat.repository.FileMetadataRepository;
import com.example.springbootpractice.domain.chat.repository.MessageLogRepository;
import com.example.springbootpractice.domain.chat.repository.MessageRepository;
import com.example.springbootpractice.domain.chat.repository.MessageTextContentRepository;
import com.example.springbootpractice.domain.chat.repository.RoomParticipantRepository;
import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.emoticon.entity.EmojiMetadata;
import com.example.springbootpractice.domain.emoticon.repository.EmojiMetadataRepository;
import com.example.springbootpractice.domain.emoticon.repository.EmojiRepository;
import com.example.springbootpractice.domain.emoticon.service.EmojiService;
import com.example.springbootpractice.domain.gift.entity.GiftMetadata;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import com.example.springbootpractice.domain.gift.repository.GiftMetadataRepository;
import com.example.springbootpractice.domain.gift.repository.VoucherRepository;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private static final Set<String> VALID_MESSAGE_TYPES = Set.of("TEXT", "IMAGE", "FILE", "EMOJI", "GIFT");
    private static final int DEFAULT_PAGE_SIZE = 30;

    private final MessageRepository messageRepository;
    private final MessageTextContentRepository messageTextContentRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final MessageLogRepository messageLogRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserRepository userRepository;
    private final EmojiRepository emojiRepository;
    private final EmojiMetadataRepository emojiMetadataRepository;
    private final EmojiService emojiService;
    private final VoucherRepository voucherRepository;
    private final GiftMetadataRepository giftMetadataRepository;

    @Transactional
    public MessageResponse send(Long userId, Long roomId, String messageType, String content, String fileUrl,
                                 String originalName, Long fileSize, Long parentMessageId, Long emojiId,
                                 Long voucherId) {
        RoomParticipant participant = roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        if (!VALID_MESSAGE_TYPES.contains(messageType)) {
            throw new BusinessException(ErrorCode.INVALID_MESSAGE_TYPE);
        }

        if (parentMessageId != null) {
            Message parent = messageRepository.findById(parentMessageId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));
            if (!parent.getRoom().getId().equals(roomId)) {
                throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
            }
        }

        ChatRoom room = participant.getRoom();
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Message message = messageRepository.save(Message.create(room, sender, messageType, parentMessageId));

        String responseContent = null;
        FileMetadata responseFileMetadata = null;
        Emoji responseEmoji = null;
        Voucher responseVoucher = null;
        String rawContent;

        switch (messageType) {
            case "TEXT" -> {
                if (content == null || content.isBlank()) {
                    throw new BusinessException(ErrorCode.INVALID_MESSAGE_CONTENT);
                }
                messageTextContentRepository.save(MessageTextContent.create(message, content));
                responseContent = content;
                rawContent = content;
            }
            case "IMAGE", "FILE" -> {
                if (fileUrl == null || originalName == null || fileSize == null) {
                    throw new BusinessException(ErrorCode.INVALID_MESSAGE_CONTENT);
                }
                responseFileMetadata =
                        fileMetadataRepository.save(FileMetadata.create(message, fileUrl, originalName, fileSize));
                rawContent = originalName;
            }
            case "EMOJI" -> {
                if (emojiId == null) {
                    throw new BusinessException(ErrorCode.INVALID_MESSAGE_CONTENT);
                }
                Emoji emoji = emojiRepository.findById(emojiId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.EMOJI_NOT_FOUND));
                if (!emojiService.canUse(userId, emojiId)) {
                    throw new BusinessException(ErrorCode.EMOJI_NOT_USABLE);
                }
                emojiMetadataRepository.save(EmojiMetadata.create(message, emoji));
                responseEmoji = emoji;
                rawContent = emoji.getCategory();
            }
            case "GIFT" -> {
                if (voucherId == null) {
                    throw new BusinessException(ErrorCode.INVALID_MESSAGE_CONTENT);
                }
                Voucher voucher = voucherRepository.findById(voucherId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
                if (!voucher.getOrder().getSender().getId().equals(userId)) {
                    throw new BusinessException(ErrorCode.VOUCHER_NOT_FOUND);
                }
                if (giftMetadataRepository.existsByVoucherId(voucherId)) {
                    throw new BusinessException(ErrorCode.VOUCHER_ALREADY_USED);
                }
                giftMetadataRepository.save(GiftMetadata.create(message, voucher));
                responseVoucher = voucher;
                rawContent = voucher.getOrder().getProduct().getProductName();
            }
            default -> throw new BusinessException(ErrorCode.INVALID_MESSAGE_TYPE);
        }

        messageLogRepository.save(MessageLog.create(message.getId(), roomId, userId, "CREATE", rawContent));

        return MessageResponse.of(message, responseContent, responseFileMetadata, responseEmoji, responseVoucher);
    }

    public List<MessageResponse> findMessages(Long userId, Long roomId, Long before, int size) {
        roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        List<Message> messages =
                messageRepository.findPage(roomId, before, PageRequest.ofSize(size <= 0 ? DEFAULT_PAGE_SIZE : size));
        List<Long> messageIds = messages.stream().map(Message::getId).toList();

        Map<Long, String> textContents = messageTextContentRepository.findAllById(messageIds).stream()
                .collect(Collectors.toMap(MessageTextContent::getMessageId, MessageTextContent::getContent));
        Map<Long, FileMetadata> fileMetadataByMessageId = fileMetadataRepository.findAllByMessageIdIn(messageIds).stream()
                .collect(Collectors.toMap(fm -> fm.getMessage().getId(), fm -> fm));
        Map<Long, Emoji> emojiByMessageId = emojiMetadataRepository.findAllByMessageIdIn(messageIds).stream()
                .collect(Collectors.toMap(em -> em.getMessage().getId(), EmojiMetadata::getEmoji));
        Map<Long, Voucher> voucherByMessageId = giftMetadataRepository.findAllByMessageIdIn(messageIds).stream()
                .collect(Collectors.toMap(gm -> gm.getMessage().getId(), GiftMetadata::getVoucher));

        return messages.stream()
                .map(m -> MessageResponse.of(m, textContents.get(m.getId()), fileMetadataByMessageId.get(m.getId()),
                        emojiByMessageId.get(m.getId()), voucherByMessageId.get(m.getId())))
                .toList();
    }

    @Transactional
    public void delete(Long userId, Long roomId, Long messageId) {
        roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));

        if (!message.getRoom().getId().equals(roomId)) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        if (!message.getSender().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        if (message.isDeleted()) {
            throw new BusinessException(ErrorCode.MESSAGE_ALREADY_DELETED);
        }

        message.delete();
        messageLogRepository.save(MessageLog.create(messageId, roomId, userId, "DELETE", "deleted"));
    }
}
