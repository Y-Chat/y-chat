package com.ychat.ychat.controllers;

import com.openapi.gen.messaging.api.MessagesApi;
import com.openapi.gen.messaging.dto.GetMessages200Response;
import com.openapi.gen.messaging.dto.PageInfo;
import com.ychat.ychat.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("${openapi.yChatMessaging.base-path:}")
public class MessagesController implements MessagesApi {

    private final MessagingService messagingService;

    public MessagesController(@Autowired(required = true) MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
   public ResponseEntity<GetMessages200Response> getMessages(UUID chatId, LocalDateTime fromDate, Integer page, Integer pageSize) {
       var res = messagingService.getMessages(chatId, fromDate, page, pageSize);
       return ResponseEntity.ok(new GetMessages200Response(res.getFirst().orElse(List.of()), new PageInfo(res.getSecond().getPageNumber(), res.getSecond().getPageSize())));
   }
}
