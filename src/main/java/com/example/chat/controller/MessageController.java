package com.example.chat.controller;

import com.example.chat.dto.MessageDto;
import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import com.example.chat.service.ConversationService;
import com.example.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api-v1.url}/messages")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final ConversationService conversationService;

    @GetMapping("/{conversationId}")
    public List<MessageDto> getAllInConversation(@PathVariable UUID conversationId, @AuthenticationPrincipal User user) {
        Conversation conversation = conversationService.getById(conversationId);
        if (!conversation.getFirstUser().getId().equals(user.getId()) && !conversation.getSecondUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unknown conversation");
        }

        return messageService.getAllDtoInConversation(conversationId);
    }
}
