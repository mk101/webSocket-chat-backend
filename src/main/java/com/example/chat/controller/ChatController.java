package com.example.chat.controller;

import com.example.chat.dto.ConversationDto;
import com.example.chat.dto.MessageDto;
import com.example.chat.model.User;
import com.example.chat.service.ConversationService;
import com.example.chat.service.MessageService;
import com.example.chat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload @Valid MessageDto message, Principal sender) {
        log.info("Received message {}", message);
        MessageDto processed = messageService.saveMessage(message);

        messagingTemplate.convertAndSendToUser(
                sender.getName(),
                "/queue/chat",
                processed
        );

        User recipient = (User) userService.loadUserById(UUID.fromString(message.getUserId()));
        messagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
                "/queue/chat",
                processed
        );
    }

    @MessageMapping("/conversation")
    public void processConversation(@Payload @Valid ConversationDto conversation) {
        log.info("Received conversation {}", conversation);
        ConversationDto conversationDto = conversationService.create(conversation);

        User user1 = (User) userService.loadUserById(UUID.fromString(conversationDto.getFirstUserId()));
        User user2 = (User) userService.loadUserById(UUID.fromString(conversationDto.getSecondUserId()));

        messagingTemplate.convertAndSendToUser(
                user1.getUsername(),
                "/queue/conversation",
                conversationDto
        );

        messagingTemplate.convertAndSendToUser(
                user2.getUsername(),
                "/queue/conversation",
                conversationDto
        );
    }
}
