package com.ychat.ychat.delegates;

import org.openapitools.api.MessagingApiDelegate;
import org.openapitools.model.Message;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class MessagingApiImplementation implements MessagingApiDelegate {

    @Override
    public ResponseEntity<Message> getMessages(String chatId, LocalDateTime fromDate) {
        // TODO BST
        return MessagingApiDelegate.super.getMessages(chatId, fromDate);
    }

    @Override
    public ResponseEntity<Message> sendMessage(Message message) {
        // TODO BST
        return MessagingApiDelegate.super.sendMessage(message);
    }
}
