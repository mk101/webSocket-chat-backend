package com.example.chat.mapper;

import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "username", source = "login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "refreshToken", source = "refreshToken")
    User mapWithoutId(UserDto userDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserDto map(User user);
}
