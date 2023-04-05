package com.example.chat;

import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserTests {
    @Value("${api-v1.url}/users")
    private String url;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UUID id1 = UUID.fromString("367b2dd9-3a12-450e-bbd6-22838f94716c");
    private final UUID id2 = UUID.fromString("f5d2cd07-ce4f-45d1-8b1f-20a54c2650c1");

    private User user1 = null;
    private User user2 = null;

    @BeforeEach
    public void init() {
        user1 = new User(id1, "first", passwordEncoder.encode("root"), "first", "", null);
        user2 = new User(id2, "second", passwordEncoder.encode("root"), "two word", "", null);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void anonymousRequest() throws Exception {
        mockMvc.perform(get(url + "/367b2dd9-3a12-450e-bbd6-22838f94716c").with(anonymous())).andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mockMvc.perform(get(url + "/byUsername/first").with(anonymous())).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void current() throws Exception {
        MvcResult result = mockMvc.perform(get(url + "/367b2dd9-3a12-450e-bbd6-22838f94716c").with(user(user1))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        UserDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(dto.getLogin()).isNotNull();

        result = mockMvc.perform(get(url + "/byUsername/first").with(user(user1))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        UserDto dtoUsername = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(dtoUsername.getLogin()).isNotNull();

        assertThat(dto).isEqualTo(dtoUsername);
    }

    @Test
    public void different() throws Exception {
        MvcResult result = mockMvc.perform(get(url + "/367b2dd9-3a12-450e-bbd6-22838f94716c").with(user(user2))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        UserDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(dto.getLogin()).isNull();

        result = mockMvc.perform(get(url + "/byUsername/first").with(user(user2))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        UserDto dtoUsername = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(dtoUsername.getLogin()).isNull();

        assertThat(dto).isEqualTo(dtoUsername);
    }

    @Test
    public void wrong() throws Exception {
        mockMvc.perform(get(url + "/" + UUID.randomUUID()).with(user(user1))).andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(get(url + "/byUsername/wrong").with(user(user1))).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
