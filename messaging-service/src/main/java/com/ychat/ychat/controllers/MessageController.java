package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessageApi;
import com.openapi.gen.messaging.dto.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("${openapi.yChatMessaging.base-path:}")
public class MessageController implements MessageApi {

    @Override
    public ResponseEntity<Message> sendMessage(com.openapi.gen.messaging.dto.Message message) {
        return MessageApi.super.sendMessage(message);
    }
}
