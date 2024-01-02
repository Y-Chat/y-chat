package com.ychat.ychat.delegates;

import com.openapi.gen.springboot.api.MessagingApiDelegate;
import com.openapi.gen.springboot.dto.Message;
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
