package com.example.chat.controller;

import com.example.chat.dto.ConversationDto;
import com.example.chat.model.User;
import com.example.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api-v1.url}/conversations")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping()
    public List<ConversationDto> getAll(@AuthenticationPrincipal User user) {
        return conversationService.getAllDtoByUser(user);
    }
}
