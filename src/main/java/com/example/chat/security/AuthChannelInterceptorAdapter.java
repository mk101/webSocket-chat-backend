package com.example.chat.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null || token.isEmpty()) {
                throw new AuthenticationCredentialsNotFoundException("Token was null or empty.");
            }

            token = token.split(" ")[1].trim();
            UUID uuid;
            try {
                uuid = jwtUtils.getSubject(token);
            } catch (JWTVerificationException e) {
                throw new BadCredentialsException("Bad Jwt token");
            }

            User user = userRepository.findById(uuid).orElse(null);

            if (user == null) {
                return null;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    user, token, Collections.singleton((GrantedAuthority) () -> "USER")
            );

            accessor.setUser(authentication);
        }

        return message;
    }
}
