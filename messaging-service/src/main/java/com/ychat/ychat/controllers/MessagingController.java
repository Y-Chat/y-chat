package com.ychat.ychat.controllers;

import com.openapi.gen.springboot.api.MessagingApi;
import com.openapi.gen.springboot.api.MessagingApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
