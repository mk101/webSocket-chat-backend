package com.example.chat.service;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.chat.dto.LoginPasswordDto;
import com.example.chat.dto.RefreshDto;
import com.example.chat.dto.TokenDto;
import com.example.chat.dto.UserDto;
import com.example.chat.exception.RefreshTokenSessionException;
import com.example.chat.exception.UserAlreadyExistsException;
import com.example.chat.mapper.UserMapper;
import com.example.chat.model.User;
import com.example.chat.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public TokenDto login(LoginPasswordDto loginPasswordDto) throws UsernameNotFoundException, JWTCreationException {
        User user = (User) userService.loadUserByUsername(loginPasswordDto.getLogin());
        if (!passwordEncoder.matches(loginPasswordDto.getPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("Wrong login or password");
        }

        return generateTokenDto(user);
    }

    public TokenDto refresh(RefreshDto refreshDto) throws UsernameNotFoundException, JWTVerificationException, RefreshTokenSessionException {
        UUID id = jwtUtils.getSubject(refreshDto.getRefresh());
        User user = (User) userService.loadUserById(id);

        if (!user.getRefreshToken().equals(refreshDto.getRefresh())) {
            throw new RefreshTokenSessionException("Refresh token doesn't match with user's");
        }

        return generateTokenDto(user);
    }

    public TokenDto register(UserDto userDto) throws UserAlreadyExistsException, JWTCreationException {
        User user = userMapper.mapWithoutId(userDto);
        user.setId(UUID.randomUUID());
        String access = jwtUtils.generateAccessToken(user.getId());
        String refresh = jwtUtils.generateRefreshToken(user.getId());

        user.setRefreshToken(refresh);
        userService.createUser(user);

        return new TokenDto(user.getId(), access, refresh);
    }

    private TokenDto generateTokenDto(User user) throws JWTCreationException, UsernameNotFoundException {
        String access = jwtUtils.generateAccessToken(user.getId());
        String refresh = jwtUtils.generateRefreshToken(user.getId());

        user.setRefreshToken(refresh);
        userService.updateUser(user);

        return new TokenDto(user.getId(), access, refresh);
    }
}
