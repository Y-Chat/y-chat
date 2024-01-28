package com.ychat.ychat.services;

import com.asyncapi.gen.notification.model.Notification;
import com.openapi.gen.calling.dto.Call;
import com.ychat.ychat.repositories.CallMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class CallService {

    private final CallMessageRepository callMessageRepository;

    private final NotificationServiceConnector notificationServiceConnector;

    private final Random random = new Random();

    public CallService(@Autowired CallMessageRepository callMessageRepository, @Autowired NotificationServiceConnector notificationServiceConnector) {
        this.callMessageRepository = callMessageRepository;
        this.notificationServiceConnector = notificationServiceConnector;
    }

    public Call createCall(Call callRequest) {
        com.ychat.ychat.models.Call call = new com.ychat.ychat.models.Call(
                UUID.randomUUID(),
                callRequest.getCallerId(),
                callRequest.getCalleeId(),
                LocalDateTime.now()
        );
        callMessageRepository.save(call);

        Notification notification = new Notification();
        notificationServiceConnector.onNotification(random.nextInt(), notification);

        return call.toOpenAPI();
    }

}
