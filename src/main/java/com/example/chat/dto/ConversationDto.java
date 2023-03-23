package com.example.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private String id;

    @JsonProperty("first_user_id")
    @NotBlank(message = "first_user_id is required")
    private String firstUserId;

    @JsonProperty("second_user_id")
    @NotBlank(message = "second_user_id is required")
    private String secondUserId;
}
