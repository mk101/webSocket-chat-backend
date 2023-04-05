package com.example.chat;

import com.example.chat.dto.ConversationDto;
import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConversationTests {
    @Value("${api-v1.url}/conversations")
    private String url;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UUID id1 = UUID.fromString("367b2dd9-3a12-450e-bbd6-22838f94716c");
    private final UUID id2 = UUID.fromString("f5d2cd07-ce4f-45d1-8b1f-20a54c2650c1");

    private User user1 = null;
    private User user2 = null;

    @BeforeEach
    public void init() {
        user1 = new User(id1, "first", passwordEncoder.encode("root"), "first", "", null);
        user2 = new User(id2, "second", passwordEncoder.encode("root"), "second", "", null);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void anonymousRequest() throws Exception {
        mockMvc.perform(get(url).with(anonymous())).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void noConversations() throws Exception {
        MvcResult result = mockMvc.perform(get(url).with(user(user1))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<ConversationDto> conversationDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(conversationDtos.size()).isEqualTo(0);
    }

    @Test
    public void hasConversation() throws Exception {
        conversationRepository.save(new Conversation(UUID.randomUUID(), user1, user2, null));

        MvcResult result = mockMvc.perform(get(url).with(user(user1))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<ConversationDto> conversationDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(conversationDtos.size()).isEqualTo(1);
    }
}
