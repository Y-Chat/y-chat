package com.ychat.ychat.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MESSAGES")
public class Message {
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
}
