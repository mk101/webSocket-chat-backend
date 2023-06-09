package com.example.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_user", nullable = false)
    private User firstUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_user", nullable = false)
    private User secondUser;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "conversation")
    List<Message> messages;
}
