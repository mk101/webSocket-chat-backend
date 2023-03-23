package com.example.chat.repository;

import com.example.chat.model.Message;
import com.example.chat.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    List<Message> findAllByUser(User user);
}
