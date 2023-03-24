package com.example.chat.mapper;

import com.example.chat.dto.MessageDto;
import com.example.chat.model.Conversation;
import com.example.chat.model.Message;
import com.example.chat.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Timestamp;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "user", target = "userId")
    @Mapping(source = "conversation", target = "conversationId")
    MessageDto map(Message message);

    default String map(UUID id) {
        return id.toString();
    }

    default long map(Timestamp ts) {
        return ts.getTime();
    }

    default String map(User user) {
        return user.getId().toString();
    }

    default String map(Conversation conversation) {
        return conversation.getId().toString();
    }
}
