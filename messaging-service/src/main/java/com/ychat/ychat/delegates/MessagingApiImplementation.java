package com.ychat.ychat.delegates;

import org.openapitools.api.MessagingApiDelegate;
import org.openapitools.model.Message;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MessagingApiImplementation implements MessagingApiDelegate {

    @Override
    public ResponseEntity<List<Message>> getMessages(UUID chatId, LocalDateTime fromDate) {
        return MessagingApiDelegate.super.getMessages(chatId, fromDate);
    }

    @Override
    public ResponseEntity<Message> sendMessage(Message message) {
        // TODO BST
        return MessagingApiDelegate.super.sendMessage(message);
    }
}
