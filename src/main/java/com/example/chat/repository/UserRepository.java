package com.example.chat.repository;

import com.example.chat.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    boolean existsUserByUsername(String username);

    Optional<User> findUserByUsername(String username);

    @EntityGraph(attributePaths = {"messages"})
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findFullUserByUsername(@Param("username") String username);
}
