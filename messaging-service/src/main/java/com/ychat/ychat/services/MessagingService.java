package com.ychat.ychat.services;

import com.asyncapi.gen.notification.model.AnonymousSchema1;
import com.asyncapi.gen.notification.model.Notification;
import com.openapi.gen.messaging.dto.MessageFetchDirection;
import com.ychat.ychat.repositories.ChatMessageRepository;
import com.openapi.gen.messaging.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    private final ChatMessageRepository messageRepository;

    private final NotificationServiceConnector notificationServiceConnector;

    private final Random random = new Random();

    public MessagingService(
            @Autowired ChatMessageRepository messageRepository,
            @Autowired NotificationServiceConnector notificationServiceConnector
    ){
        this.messageRepository = messageRepository;
        this.notificationServiceConnector = notificationServiceConnector;
    }

    public Pair<Optional<List<Message>>, PageRequest> getMessages(UUID chatId, OffsetDateTime fromDate, Integer page, Integer pageSize, MessageFetchDirection direction) {
        if(page == null) {
            page = 0;
        }
        if(pageSize == null) {
            pageSize = 20;
        }

        if(direction == null) {
            direction = MessageFetchDirection.FUTURE;
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        var res = (
                direction.equals(MessageFetchDirection.FUTURE) ?
                        messageRepository.findByChatIdAndSentTimestampAfterOrderBySentTimestampDesc(chatId, PageRequest.of(page, pageSize), fromDate.toInstant()) :
                        messageRepository.findByChatIdAndSentTimestampBeforeOrderBySentTimestampDesc(chatId, PageRequest.of(page, pageSize), fromDate.toInstant())
                )
                .stream()
                .map(com.ychat.ychat.models.Message::toOpenAPI)
                .collect(Collectors.toList());
        return Pair.of(Optional.of(res), pageRequest);
    }

    public Optional<Message> sendMessage(Message message, UUID senderId) {
        // TODO Check if senderId is part of chat
        // TODO Check if transactionId is valid (Do when/if payment service is implemented)
        com.ychat.ychat.models.Message newMessage = new com.ychat.ychat.models.Message(
                UUID.randomUUID(),
                senderId,
                message.getChatId(),
                OffsetDateTime.now().toInstant(),
                message.getMessage(),
                message.getMediaPath(),
                message.getTransactionId()
        );
        newMessage = messageRepository.save(newMessage);

        var notification = new Notification();
        var schema1 = new AnonymousSchema1();
        schema1.setChatId(newMessage.getChatId().toString());
        notification.setNewMessage(schema1);
        notificationServiceConnector.onNotification(random.nextInt(), notification);

        return Optional.of(newMessage.toOpenAPI());
    }
}
