package com.example.chat;

import com.example.chat.utils.TimeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

public class TimeUtilsTests {
    private static TimeUtils timeUtils;

    @BeforeAll
    public static void init() {
        timeUtils = new TimeUtils();
    }

    @Test
    public void timeCorrect() {
        String minutes = "10m";
        Instant now = Instant.now();
        assertThat(timeUtils.parseString(minutes)).isBetween(now, now.plus(10, ChronoUnit.MINUTES));

        String days = "30d";
        now = Instant.now();
        assertThat(timeUtils.parseString(days)).isBetween(now, now.plus(30, ChronoUnit.DAYS));
    }

    @Test
    public void timeIncorrect() {
        assertThatThrownBy(() -> timeUtils.parseString("-10m")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> timeUtils.parseString("m")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("m");
        assertThatThrownBy(() -> timeUtils.parseString("42s")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("42s");
        assertThatThrownBy(() -> timeUtils.parseString("30M")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("30M");
    }

}
