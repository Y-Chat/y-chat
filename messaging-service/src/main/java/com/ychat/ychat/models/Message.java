package com.ychat.ychat.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document()
public class Message {
    public Message(UUID id, UUID senderId, UUID chatId, LocalDateTime sentTimestamp, String message, UUID mediaId, UUID transactionId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.sentTimestamp = sentTimestamp;
        this.message = message;
        this.mediaId = mediaId;
        this.transactionId = transactionId;
    }

    @Id
    private UUID id;

    private UUID senderId;

    private UUID chatId;

    private LocalDateTime sentTimestamp;

    private String message;

    private UUID mediaId;

    private UUID transactionId;

    public Message() {

    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChatId() {
        return chatId;
    }

    public LocalDateTime getSentTimestamp() {
        return sentTimestamp;
    }

    public String getMessage() {
        return message;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public com.openapi.gen.messaging.dto.Message toOpenAPI() {
        return new com.openapi.gen.messaging.dto.Message(
                this.id,
                this.senderId,
                this.chatId,
                this.sentTimestamp,
                this.message
        );
    }

    public static Message fromOpenAPI(com.openapi.gen.messaging.dto.Message message) {
        return new Message(
                message.getId(),
                message.getSenderId(),
                message.getChatId(),
                message.getSentTimestamp(),
                message.getMessage(),
                message.getMediaId(),
                message.getTransactionId()
        );
    }
}
