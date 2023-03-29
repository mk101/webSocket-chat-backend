package com.example.chat;

import com.example.chat.utils.JwtUtils;
import com.example.chat.utils.TimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"jwt.access-live=2m", "jwt.refresh-live=10d", "jwt.secret=TEST"})
@ContextConfiguration(classes = JwtTests.Config.class)
public class JwtTests {
    @Configuration
    static class Config {
        @Bean
        public TimeUtils timeUtils() {
            return Mockito.mock(TimeUtils.class);
        }

        @Bean
        public JwtUtils jwtUtils() {
            return new JwtUtils(timeUtils());
        }
    }

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TimeUtils timeUtils;

    @BeforeEach
    public void setupTime() {
        Mockito.when(timeUtils.parseString("2m")).thenReturn(Instant.now().plus(2, ChronoUnit.MINUTES));
        Mockito.when(timeUtils.parseString("10d")).thenReturn(Instant.now().plus(10, ChronoUnit.DAYS));
    }

    @Test
    public void create() {
        UUID uuid = UUID.randomUUID();
        String accessToken = jwtUtils.generateAccessToken(uuid);
        String refreshToken = jwtUtils.generateRefreshToken(uuid);

        assertThat(accessToken.split("\\.")).hasSize(3);
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    public void validateAccess() {
        UUID uuid = UUID.randomUUID();

        String accessToken = jwtUtils.generateAccessToken(uuid);
        assertThat(jwtUtils.getSubject(accessToken)).isEqualTo(uuid);
    }

    @Test
    public void validateRefresh() {
        UUID uuid = UUID.randomUUID();

        String refreshToken = jwtUtils.generateRefreshToken(uuid);
        assertThat(jwtUtils.getSubject(refreshToken)).isEqualTo(uuid);
    }
}
