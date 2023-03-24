package com.example.chat.controller;

import com.example.chat.dto.ErrorDto;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(assignableTypes = {
        ChatController.class
})
public class MessageControllerHandler {
    @MessageExceptionHandler
    @SendToUser(value = "/queue/error", broadcast = false)
    public ErrorDto handle(RuntimeException e) {
        return new ErrorDto(e.getMessage());
    }
}
