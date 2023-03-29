package com.example.chat;

import com.example.chat.dto.LoginPasswordDto;
import com.example.chat.dto.RefreshDto;
import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import com.example.chat.utils.JwtUtils;
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
import com.example.chat.repository.UserRepository;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AuthTests {
    @Value("${api-v1.url}")
    private String url;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UUID uuid = UUID.fromString("367b2dd9-3a12-450e-bbd6-22838f94716c");

    @BeforeEach
    public void init() {
        String refresh = jwtUtils.generateRefreshToken(uuid);
        userRepository.save(new User(uuid, "root", passwordEncoder.encode("root"), "root user", refresh, null));
    }

    @Test
    public void registryValid() throws Exception {
        UserDto userDto = new UserDto(null, "test", "test", "this is test", null);

        mockMvc.perform(post(url + "/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User user = userRepository.findUserByUsername("test").orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void registryInvalid() throws Exception {
        UserDto userDto = new UserDto(null, null, "test", "this is test", null);
        mockMvc.perform(post(url + "/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registryUserExists() throws Exception {
        UserDto userDto = new UserDto(null, "root", "root", "this is test", null);
        mockMvc.perform(post(url + "/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void loginValid() throws Exception {
        Thread.sleep(1000);
        String startToken = userRepository.findUserByUsername("root").orElseThrow().getRefreshToken();
        LoginPasswordDto loginPasswordDto = new LoginPasswordDto("root", "root");
        mockMvc.perform(post(url + "/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginPasswordDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User user = userRepository.findUserByUsername("root").orElseThrow();
        assertThat(user.getRefreshToken()).isNotEqualTo(startToken);
    }

    @Test
    public void loginInvalid() throws Exception {
        LoginPasswordDto loginPasswordDto = new LoginPasswordDto(null, "root");
        mockMvc.perform(post(url + "/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginPasswordDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void refreshValid() throws Exception {
        Thread.sleep(1000);
        String startToken = userRepository.findUserByUsername("root").orElseThrow().getRefreshToken();
        RefreshDto refreshDto = new RefreshDto(startToken);
        mockMvc.perform(post(url + "/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        User user = userRepository.findUserByUsername("root").orElseThrow();
        assertThat(user.getRefreshToken()).isNotEqualTo(startToken);
    }

    @Test
    public void refreshInvalid() throws Exception {
        RefreshDto refreshDto = new RefreshDto("abc.yty.tre");
        mockMvc.perform(post(url + "/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void refreshInvalidNull() throws Exception {
        RefreshDto refreshDto = new RefreshDto(null);
        mockMvc.perform(post(url + "/auth/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
