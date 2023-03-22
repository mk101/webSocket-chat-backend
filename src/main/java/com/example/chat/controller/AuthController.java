package com.example.chat.controller;

import com.example.chat.dto.LoginPasswordDto;
import com.example.chat.dto.RefreshDto;
import com.example.chat.dto.TokenDto;
import com.example.chat.dto.UserDto;
import com.example.chat.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-v1.url}")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginPasswordDto loginPasswordDto) {
        return authService.login(loginPasswordDto);
    }

    @PostMapping("/register")
    public TokenDto register(@Valid @RequestBody UserDto userDto) {
        return authService.register(userDto);
    }

    @PostMapping("/refresh")
    public TokenDto refresh(@Valid @RequestBody RefreshDto refreshDto) {
        return authService.refresh(refreshDto);
    }

}
