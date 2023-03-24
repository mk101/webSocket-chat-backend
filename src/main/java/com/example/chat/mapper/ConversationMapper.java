package com.example.chat.mapper;

import com.example.chat.dto.ConversationDto;
import com.example.chat.model.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstUser.id", target = "firstUserId")
    @Mapping(source = "secondUser.id", target = "secondUserId")
    ConversationDto map(Conversation conversation);
}
