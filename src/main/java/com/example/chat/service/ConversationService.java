package com.example.chat.service;

import com.example.chat.dto.ConversationDto;
import com.example.chat.mapper.ConversationMapper;
import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import com.example.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final ConversationMapper mapper;

    public Conversation getById(UUID id) throws IllegalArgumentException {
        return conversationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unknown Conversation"));
    }

    public ConversationDto create(ConversationDto conversationDto) throws IllegalArgumentException {
        var exist = conversationRepository.findByUsers(UUID.fromString(conversationDto.getFirstUserId()), UUID.fromString(conversationDto.getSecondUserId()))
                .orElse(null);

        if (exist != null) {
            throw new IllegalArgumentException("Conversation is already exist");
        }

        Conversation conversation = new Conversation();
        conversation.setId(UUID.randomUUID());
        conversation.setFirstUser((User) userService.loadUserById(UUID.fromString(conversationDto.getFirstUserId())));
        conversation.setSecondUser((User) userService.loadUserById(UUID.fromString(conversationDto.getSecondUserId())));

        conversationRepository.save(conversation);

        return mapper.map(conversation);
    }

    public List<ConversationDto> getAllDtoByUser(User user) {
        List<Conversation> conversations = conversationRepository.findAllByUser(user.getId());
        return conversations.stream().map(mapper::map).toList();
    }
}
