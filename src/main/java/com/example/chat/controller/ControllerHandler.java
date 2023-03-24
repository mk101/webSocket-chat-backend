package com.example.chat.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.chat.dto.ErrorDto;
import com.example.chat.exception.RefreshTokenSessionException;
import com.example.chat.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {
        AuthController.class,
        UserController.class,
        ConversationController.class
})
public class ControllerHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> catchValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "Validation error";
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        return ResponseEntity.badRequest().body(new ErrorDto(message));
    }

    @ExceptionHandler(value = {
            RefreshTokenSessionException.class,
            UserAlreadyExistsException.class,
            UsernameNotFoundException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorDto> catchExceptions(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(value = {JWTCreationException.class})
    public ResponseEntity<ErrorDto> catchJWTCreation(JWTCreationException e) {
        return ResponseEntity.badRequest().body(new ErrorDto("Failed to create token"));
    }

    @ExceptionHandler(value = {JWTVerificationException.class})
    public ResponseEntity<ErrorDto> catchJWTVerification(JWTVerificationException e) {
        return ResponseEntity.badRequest().body(new ErrorDto("Invalid token"));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDto> catchOthers(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Internal server error"));
    }
}
