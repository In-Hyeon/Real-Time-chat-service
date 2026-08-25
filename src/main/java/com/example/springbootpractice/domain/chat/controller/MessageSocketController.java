package com.example.springbootpractice.domain.chat.controller;

import com.example.springbootpractice.domain.chat.dto.MessageCreateRequest;
import com.example.springbootpractice.domain.chat.dto.MessageResponse;
import com.example.springbootpractice.domain.chat.service.MessageService;
import com.example.springbootpractice.global.exception.BusinessException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/rooms/{roomId}/messages")
    public void send(@DestinationVariable Long roomId, @Payload MessageCreateRequest request, Principal principal) {
        Long userId = Long.valueOf(principal.getName());

        MessageResponse response = messageService.send(userId, roomId, request.messageType(), request.content(),
                request.fileUrl(), request.originalName(), request.fileSize(), request.parentMessageId(),
                request.emojiId(), request.voucherId());

        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, response);
    }

    @MessageExceptionHandler(BusinessException.class)
    @SendToUser("/queue/errors")
    public String handleBusinessException(BusinessException e) {
        return e.getMessage();
    }
}
