package com.example.chat.service;

import com.example.chat.dto.MessageDto;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.model.Conversation;
import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final UserService userService;
    private final MessageMapper mapper;

    public MessageDto saveMessage(MessageDto messageDto) throws IllegalArgumentException, UsernameNotFoundException {
        Conversation conversation = conversationService.getById(UUID.fromString(messageDto.getConversationId()));
        UUID userId = UUID.fromString(messageDto.getUserId());
        if (!userId.equals(conversation.getFirstUser().getId()) && !userId.equals(conversation.getSecondUser().getId())) {
            throw new IllegalArgumentException("Unknown user");
        }

        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setConversation(conversation);
        message.setUser((User) userService.loadUserById(userId));
        message.setContent(messageDto.getContent());
        message.setTimestamp(Timestamp.from(Instant.now()));

        messageRepository.save(message);

        return mapper.map(message);
    }
}
