package com.example.chat;

import com.example.chat.dto.MessageDto;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.mapper.MessageMapperImpl;
import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.service.ConversationService;
import com.example.chat.service.MessageService;
import com.example.chat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = {SocketMessageTests.Config.class})
public class SocketMessageTests {
    @Configuration
    static class Config {
        @MockBean
        public MessageRepository messageRepository;

        @MockBean
        public ConversationService conversationService;

        @MockBean
        public UserService userService;

        @Bean
        public MessageMapper messageMapper() {
            return new MessageMapperImpl();
        }

        @Bean
        public MessageService messageService() {
            return new MessageService(messageRepository, conversationService, userService, messageMapper());
        }
    }

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    private final User user1 = new User(UUID.randomUUID(), "first", "root", "first", "", Collections.emptyList());
    private final User user2 = new User(UUID.randomUUID(), "second", "root", "second", "", Collections.emptyList());
    private final User user3 = new User(UUID.randomUUID(), "third", "root", "third", "", Collections.emptyList());
    private final Conversation conversation = new Conversation(UUID.randomUUID(), user1, user2, Collections.emptyList());

    private final UUID unknownId = UUID.randomUUID();

    @BeforeEach
    public void init() {
        when(conversationService.getById(conversation.getId())).thenReturn(conversation);
        when(conversationService.getById(unknownId)).thenThrow(IllegalArgumentException.class);
        when(userService.loadUserById(user1.getId())).thenReturn(user1);
    }

    @Test
    public void correctMessage() {
        String content = "content";

        Instant start = Instant.now();
        MessageDto result = messageService.saveMessage(new MessageDto("", content, 0, user1.getId().toString(), conversation.getId().toString()));
        Instant end = Instant.now();

        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getUserId()).isEqualTo(user1.getId().toString());
        assertThat(result.getConversationId()).isEqualTo(conversation.getId().toString());
        assertThat(result.getTimestamp()).isBetween(start.toEpochMilli(), end.toEpochMilli());
    }

    @Test
    public void wrongUser() {
        assertThatThrownBy(
                () -> messageService.saveMessage(new MessageDto("", "content", 0, user3.getId().toString(), conversation.getId().toString()))
        );
    }

    @Test
    public void wrongConversation() {
        assertThatThrownBy(
                () -> messageService.saveMessage(new MessageDto("", "content", 0, user1.getId().toString(), unknownId.toString()))
        );
    }

}
