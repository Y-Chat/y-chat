package com.ychat.ychat.controllers;

import com.openapi.gen.media.api.DataApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class DataController implements DataApi {
    @Override
    public ResponseEntity<Resource> getMedia(UUID mediaDataId) {
        // TODO
        return DataApi.super.getMedia(mediaDataId);
    }
}
