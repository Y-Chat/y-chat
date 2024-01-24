package com.ychat.ychat.repositories;

import com.ychat.ychat.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends MongoRepository<Message, UUID> {
    List<Message> findBySenderId(UUID id, Pageable pageable);

    List<Message> findByChatId(UUID id, Pageable pageable);
}
