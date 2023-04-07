package com.example.chat;


import com.example.chat.dto.ConversationDto;
import com.example.chat.mapper.ConversationMapper;
import com.example.chat.mapper.ConversationMapperImpl;
import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.service.ConversationService;
import com.example.chat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = SocketConversationTests.Config.class)
public class SocketConversationTests {
    @Configuration
    static class Config {
        @MockBean
        public ConversationRepository conversationRepository;

        @MockBean
        public UserService userService;

        @Bean
        public ConversationMapper conversationMapper() {
            return new ConversationMapperImpl();
        }

        @Bean
        public ConversationService conversationService() {
            return new ConversationService(conversationRepository, userService, conversationMapper());
        }
    }

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    private final User user1 = new User(UUID.randomUUID(), "first", "root", "first", "", Collections.emptyList());
    private final User user2 = new User(UUID.randomUUID(), "second", "root", "second", "", Collections.emptyList());
    private final User user3 = new User(UUID.randomUUID(), "third", "root", "third", "", Collections.emptyList());

    @BeforeEach
    public void init() {
        when(conversationRepository.findByUsers(user1.getId(), user2.getId())).thenReturn(Optional.empty());
        when(conversationRepository.findByUsers(user2.getId(), user3.getId())).thenReturn(Optional.of(new Conversation(UUID.randomUUID(), user2, user3, Collections.emptyList())));

        when(userService.loadUserById(user1.getId())).thenReturn(user1);
        when(userService.loadUserById(user2.getId())).thenReturn(user2);
    }

    @Test
    public void correctCreate() {
        ConversationDto result = conversationService.create(new ConversationDto(null, user1.getId().toString(), user2.getId().toString()));

        assertThat(result.getFirstUserId()).isEqualTo(user1.getId().toString());
        assertThat(result.getSecondUserId()).isEqualTo(user2.getId().toString());
    }

    @Test
    public void createExistConversation() {
        assertThatThrownBy(
                () -> conversationService.create(new ConversationDto(null, user2.getId().toString(), user3.getId().toString()))
        );
    }
}
