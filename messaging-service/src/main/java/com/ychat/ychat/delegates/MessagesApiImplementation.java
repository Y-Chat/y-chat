package com.ychat.ychat.delegates;

import com.openapi.gen.messaging.api.MessagesApiDelegate;
import com.openapi.gen.messaging.dto.GetMessages200Response;
import com.openapi.gen.messaging.dto.Message;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MessagesApiImplementation implements MessagesApiDelegate {

    @Override
    public ResponseEntity<GetMessages200Response> getMessages(UUID chatId, LocalDateTime fromDate, Integer page, Integer pageSize) {
        return MessagesApiDelegate.super.getMessages(chatId, fromDate, page, pageSize);
    }
}
