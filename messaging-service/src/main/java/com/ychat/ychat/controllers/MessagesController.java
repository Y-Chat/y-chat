package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessagesApi;
import com.openapi.gen.messaging.dto.GetMessages200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("${openapi.yChatMessaging.base-path:}")
public class MessagesController implements MessagesApi {
   public ResponseEntity<GetMessages200Response> getMessages(UUID chatId, LocalDateTime fromDate, Integer page, Integer pageSize) {
       return MessagesApi.super.getMessages(chatId, fromDate, page, pageSize);
   }
}
