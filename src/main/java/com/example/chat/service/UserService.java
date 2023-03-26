package com.example.chat.service;

import com.example.chat.dto.UserDto;
import com.example.chat.exception.UserAlreadyExistsException;
import com.example.chat.mapper.UserMapper;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void createUser(UserDetails user) throws UserAlreadyExistsException {
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistsException(String.format("%s already exists", user.getUsername()));
        }

        ((User)user).setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save((User)user);
    }

    @Override
    public void updateUser(UserDetails user) throws UsernameNotFoundException {
        if(!userExists(user.getUsername())) {
            throw new UsernameNotFoundException(String.format("%s not found", user.getUsername()));
        }

        userRepository.save((User)user);
    }

    @Override
    public void deleteUser(String username) {
        if (!userExists(username)) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        User user = (User) loadUserByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found", username)));
    }

    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDto loadUserDtoById(UUID id) throws UsernameNotFoundException {
        User user = (User) loadUserById(id);
        return userMapper.map(user);
    }

    public UserDto loadUserDtoByUsername(String username) throws UsernameNotFoundException {
        User user = (User) loadUserByUsername(username);
        return userMapper.map(user);
    }
}
