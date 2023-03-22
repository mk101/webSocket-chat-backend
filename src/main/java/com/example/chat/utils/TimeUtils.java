package com.example.chat.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TimeUtils {

    public Instant parseString(String str) throws IllegalArgumentException {
        char type = str.charAt(str.length() - 1);
        int number;

        try {
            number = Integer.parseInt(str.substring(0, str.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("String %s have wrong format", str));
        }

        if (number <= 0) {
            throw new IllegalArgumentException();
        }

        return switch (type) {
            case 'm' -> Instant.now().plus(number, ChronoUnit.MINUTES);
            case 'd' -> Instant.now().plus(number, ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException(String.format("String %s have wrong format", str));
        };
    }
}
