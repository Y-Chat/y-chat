package com.ychat.ychat.controllers;

import com.asyncapi.gen.notification.model.Notification;
import com.asyncapi.gen.notification.service.MessageHandlerService;
import com.ychat.ychat.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationKafkaController extends MessageHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationKafkaController.class);

    private final NotificationService notificationService;

    public NotificationKafkaController(@Autowired NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void createNotification(@Payload Notification payload,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_KEY) Integer key,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        LOGGER.info("Key: " + key + ", Payload: " + payload.toString() + ", Timestamp: " + timestamp + ", Partition: " + partition);
        notificationService.createNotification(payload);
    }
}
