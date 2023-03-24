package com.example.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String id;

    @NotBlank(message = "content is required")
    private String content;

    private long timestamp;

    @JsonProperty("user_id")
    @NotBlank(message = "user_id is required")
    private String userId;

    @JsonProperty("conversation_id")
    @NotBlank(message = "conversation_id is required")
    private String conversationId;
}
