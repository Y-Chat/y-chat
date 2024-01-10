package com.ychat.ychat.delegates;

import com.openapi.gen.messaging.api.MessageApiDelegate;
import com.openapi.gen.messaging.dto.Message;
import org.springframework.http.ResponseEntity;

public class MessageApiImplementation implements MessageApiDelegate {

    @Override
    public ResponseEntity<Message> sendMessage(Message message) {
        // TODO BST
        return MessageApiDelegate.super.sendMessage(message);
    }
}
