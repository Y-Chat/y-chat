package com.ychat.ychat.controllers;

import org.openapitools.api.MessagingApi;
import org.openapitools.api.MessagingApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("${openapi.yChat.base-path:/api/v1}")
public class MessagingController implements MessagingApi {
    private final MessagingApiDelegate delegate;

    public MessagingController(@Autowired(required = false) MessagingApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new MessagingApiDelegate() {});
    }

    @Override
    public MessagingApiDelegate getDelegate() {
        return delegate;
    }
}
