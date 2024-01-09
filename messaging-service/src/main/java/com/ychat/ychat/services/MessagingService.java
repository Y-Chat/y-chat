package com.ychat.ychat.services;

import com.ychat.ychat.repositories.MessageRepository;
import org.openapitools.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessagingService {

    @Autowired
    private MessageRepository messageRepository;

    public Optional<List<Message>> getMessages(UUID chatId, LocalDateTime fromDate) {

        return Optional.empty();
    }

    public Optional<Message> sendMessage(Message message, UUID senderId) {
        // TODO Check if senderId is part of chat
        // TODO Check if mediaId is valid
        // TODO Check if transactionId is valid
        com.ychat.ychat.models.Message newMessage = new com.ychat.ychat.models.Message(
                senderId,
                message.getChatId(),
                LocalDateTime.now(),
                message.getMessage(),
                message.getMediaId(),
                message.getTransactionId()
        );
        newMessage = messageRepository.save(newMessage);
        return Optional.of(newMessage.toOpenAPI());
    }
}
