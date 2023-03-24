package com.example.chat.repository;

import com.example.chat.model.Conversation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CrudRepository<Conversation, UUID> {
    boolean existsById(UUID id);

    @Query("SELECT c FROM Conversation c WHERE (c.firstUser.id = :user1 AND c.secondUser.id = :user2) OR (c.firstUser.id = :user2 AND c.secondUser.id = :user1)")
    Optional<Conversation> findByUsers(@Param("user1") UUID user1, @Param("user2") UUID user2);

    @Query("SELECT c FROM Conversation c WHERE c.firstUser.id = :user OR c.secondUser.id = :user")
    List<Conversation> findAllByUser(@Param("user") UUID user);
}
