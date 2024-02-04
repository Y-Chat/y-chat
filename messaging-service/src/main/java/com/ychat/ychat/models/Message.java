package com.ychat.ychat.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Document()
public class Message {
    public Message(UUID id, UUID senderId, UUID chatId, Instant sentTimestamp, String message, String mediaPath, UUID transactionId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.sentTimestamp = sentTimestamp;
        this.message = message;
        this.mediaPath = mediaPath;
        this.transactionId = transactionId;
    }

    @Id
    private UUID id;

    private UUID senderId;

    private UUID chatId;

    private Instant sentTimestamp;

    private String message;

    private String mediaPath;

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

    public Instant getSentTimestamp() {
        return sentTimestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public com.openapi.gen.messaging.dto.Message toOpenAPI() {
        var message = new com.openapi.gen.messaging.dto.Message(
                this.id,
                this.senderId,
                this.chatId,
                OffsetDateTime.ofInstant(this.sentTimestamp, OffsetDateTime.now().getOffset()),
                this.message
        );
        message.setTransactionId(this.transactionId);
        message.setMediaPath(this.mediaPath);
        return message;
    }

    public static Message fromOpenAPI(com.openapi.gen.messaging.dto.Message message) {
        return new Message(
                message.getId(),
                message.getSenderId(),
                message.getChatId(),
                message.getSentTimestamp().toInstant(),
                message.getMessage(),
                message.getMediaPath(),
                message.getTransactionId()
        );
    }
}
