package com.ychat.ychat.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
public class Call {

    public Call(UUID id, UUID callerId, UUID calleeId, LocalDateTime timestamp) {
        this.id = id;
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.timestamp = timestamp;
    }

    @Id
    private UUID id;

    private UUID callerId;

    private UUID calleeId;

    private LocalDateTime timestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCallerId() {
        return callerId;
    }

    public void setCallerId(UUID callerId) {
        this.callerId = callerId;
    }

    public UUID getCalleeId() {
        return calleeId;
    }

    public void setCalleeId(UUID calleeId) {
        this.calleeId = calleeId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public com.openapi.gen.calling.dto.Call toOpenAPI() {
        return new com.openapi.gen.calling.dto.Call(this.id, this.callerId, this.calleeId, this.timestamp);
    }
}
