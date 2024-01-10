package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessageApi;
import com.openapi.gen.messaging.dto.Message;
import com.ychat.ychat.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("${openapi.yChatMessaging.base-path:}")
public class MessageController implements MessageApi {

    private final MessagingService messagingService;

    public MessageController(@Autowired(required = true) MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public ResponseEntity<Message> sendMessage(com.openapi.gen.messaging.dto.Message message) {
        var res = messagingService.sendMessage(message, UUID.randomUUID());
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(500).build());
    }
}
