package com.ychat.ychat.controllers;

import com.openapi.gen.notification.api.NotificationApi;
import com.ychat.ychat.SecurityConfig;
import com.ychat.ychat.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController implements NotificationApi {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(@Autowired NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public ResponseEntity<Void> updateToken(String notificationToken) {
        var requesterUUID = SecurityConfig.getRequesterUUID();
        var requesterFirebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.updateToken(requesterUUID, requesterFirebaseUid, notificationToken);
        return ResponseEntity.ok().build();
    }

}
