package com.ychat.ychat.repositories;

import com.ychat.ychat.models.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends MongoRepository<Message, UUID> {
    List<Message> findBySenderId(UUID id, Pageable pageable);

    List<Message> findByChatIdAndSentTimestampBeforeOrderBySentTimestampDesc(UUID id, Pageable pageable, OffsetDateTime fromDate);

    List<Message> findByChatIdAndSentTimestampAfterOrderBySentTimestampDesc(UUID id, Pageable pageable, OffsetDateTime fromDate);
}
