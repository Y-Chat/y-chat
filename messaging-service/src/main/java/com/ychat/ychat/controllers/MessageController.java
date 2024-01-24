package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessageApi;
import com.openapi.gen.messaging.dto.Message;
import com.ychat.ychat.SecurityConfig;
import com.ychat.ychat.services.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageController implements MessageApi {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessagingService messagingService;

    public MessageController(@Autowired(required = true) MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public ResponseEntity<Message> sendMessage(com.openapi.gen.messaging.dto.Message message) {
        var requesterId = SecurityConfig.getRequesterUUID();
        // TODO Check with social service if user is allowed to access chat
        var res = messagingService.sendMessage(message, requesterId);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(500).build());
    }
}
