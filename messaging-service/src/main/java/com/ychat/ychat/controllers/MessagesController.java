package com.ychat.ychat.controllers;

import com.asyncapi.gen.notification.model.AnonymousSchema1;
import com.asyncapi.gen.notification.model.Notification;
import com.openapi.gen.messaging.api.MessagesApi;
import com.openapi.gen.messaging.dto.GetMessages200Response;
import com.openapi.gen.messaging.dto.MessageFetchDirection;
import com.openapi.gen.messaging.dto.PageInfo;
import com.ychat.ychat.SecurityConfig;
import com.ychat.ychat.services.MessagingService;
import com.ychat.ychat.services.NotificationServiceConnector;
import com.ychat.ychat.services.SocialServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("${openapi.yChatMessaging.base-path:}")
public class MessagesController implements MessagesApi {

    private static final Logger logger = LoggerFactory.getLogger(MessagesController.class);

    private final MessagingService messagingService;

    private final SocialServiceConnector socialServiceConnector;

    public MessagesController(@Autowired MessagingService messagingService, @Autowired SocialServiceConnector socialServiceConnector) {
        this.messagingService = messagingService;
        this.socialServiceConnector = socialServiceConnector;
    }

    @Override
    public ResponseEntity<GetMessages200Response> getMessages(UUID chatId, OffsetDateTime fromDate, Integer page, Integer pageSize, MessageFetchDirection direction) {
        var requesterId = SecurityConfig.getRequesterUUID();
        // TODO Check with social service if user is allowed to access chat
        var res = messagingService.getMessages(chatId, fromDate, page, pageSize, direction);
        return ResponseEntity.ok(new GetMessages200Response(res.getFirst().orElse(List.of()), new PageInfo(res.getSecond().getPageNumber(), res.getSecond().getPageSize())));
    }
}
