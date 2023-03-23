package com.example.chat.repository;

import com.example.chat.model.Conversation;
import com.example.chat.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CrudRepository<Conversation, UUID> {
    Optional<Conversation> findAllByFirstUserAndSecondUser(User firstUser, User secondUser);

    @Query("SELECT c FROM Conversation c WHERE c.firstUser = :user OR c.secondUser = :user")
    List<Conversation> findAllByUser(@Param("user") User user);
}
