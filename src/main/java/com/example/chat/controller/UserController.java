package com.example.chat.controller;

import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import com.example.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api-v1.url}/users")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        UserDto dto = userService.loadUserDtoById(id);

        if (!user.getId().equals(id)) {
            dto.setPassword(null);
            dto.setLogin(null);
            dto.setRefreshToken(null);
        }

        return dto;
    }
}
