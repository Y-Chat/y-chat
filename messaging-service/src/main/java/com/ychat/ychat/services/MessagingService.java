package com.ychat.ychat.services;

import com.asyncapi.gen.notification.model.AnonymousSchema1;
import com.asyncapi.gen.notification.model.Notification;
import com.ychat.ychat.repositories.ChatMessageRepository;
import com.openapi.gen.messaging.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private NotificationServiceConnector notificationServiceConnector;

    public Pair<Optional<List<Message>>, PageRequest> getMessages(UUID chatId, LocalDateTime fromDate, Integer page, Integer pageSize) {
        if(page == null) {
            page = 0;
        }
        if(pageSize == null) {
            pageSize = 20;
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        var res = messageRepository
                .findByChatId(chatId, PageRequest.of(page, pageSize))
                .stream()
                .map(com.ychat.ychat.models.Message::toOpenAPI)
                .collect(Collectors.toList());
        var notification = new Notification();
        var schema1 = new AnonymousSchema1();
        schema1.setChatId("70f3ca1e-8e50-4f5c-b9e1-8815e004d0ef");
        notification.setNewMessage(schema1);
        notificationServiceConnector.onNotification(1, notification);
        return Pair.of(Optional.of(res), pageRequest);
    }

    public Optional<Message> sendMessage(Message message, UUID senderId) {
        // TODO Check if senderId is part of chat
        // TODO Check if mediaId is valid
        // TODO Check if transactionId is valid
        com.ychat.ychat.models.Message newMessage = new com.ychat.ychat.models.Message(
                UUID.randomUUID(),
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
