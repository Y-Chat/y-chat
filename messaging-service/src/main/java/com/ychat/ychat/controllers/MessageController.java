package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessageApi;
import com.openapi.gen.messaging.dto.Message;
import com.ychat.ychat.SecurityConfig;
import com.ychat.ychat.services.MessagingService;
import com.ychat.ychat.services.SocialServiceConnector;
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

    private final SocialServiceConnector socialServiceConnector;

    public MessageController(@Autowired MessagingService messagingService, @Autowired SocialServiceConnector socialServiceConnector) {
        this.messagingService = messagingService;
        this.socialServiceConnector = socialServiceConnector;
    }

    @Override
    public ResponseEntity<Message> sendMessage(com.openapi.gen.messaging.dto.Message message) {
        var requesterId = SecurityConfig.getRequesterUUID();
        if(!socialServiceConnector.canUserAccessChat(requesterId, message.getChatId())) return ResponseEntity.status(401).build();
        var res = messagingService.sendMessage(message, requesterId);
        return ResponseEntity.ok(res);
    }
}
