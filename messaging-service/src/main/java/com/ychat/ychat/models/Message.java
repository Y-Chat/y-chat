package com.ychat.ychat.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MESSAGES")
public class Message {
    public Message(UUID senderId, UUID chatId, LocalDateTime sentTimestamp, String message, UUID mediaId, UUID transactionId) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.sentTimestamp = sentTimestamp;
        this.message = message;
        this.mediaId = mediaId;
        this.transactionId = transactionId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "senderId")
    private UUID senderId;

    @Column(nullable = false, name = "chatId")
    private UUID chatId;

    @Column(nullable = false, name = "sentTimestamp")
    private LocalDateTime sentTimestamp;

    @Column(nullable = false, name = "message")
    private String message;

    @Column(name = "mediaId")
    private UUID mediaId;

    @Column(name = "transactionId")
    private UUID transactionId;

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

    public org.openapitools.model.Message toOpenAPI() {
        return new org.openapitools.model.Message(
                this.id,
                this.senderId,
                this.chatId,
                this.sentTimestamp,
                this.message
        );
    }

    public static Message fromOpenAPI(org.openapitools.model.Message message) {
        return new Message(
                message.getSenderId(),
                message.getChatId(),
                message.getSentTimestamp(),
                message.getMessage(),
                message.getMediaId(),
                message.getTransactionId()
        );
    }
}
