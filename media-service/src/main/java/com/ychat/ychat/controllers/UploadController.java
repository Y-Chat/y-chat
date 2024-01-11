package com.ychat.ychat.controllers;

import com.openapi.gen.media.api.UploadApi;
import com.openapi.gen.media.dto.MediaData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController implements UploadApi {
    @Override
    public ResponseEntity<MediaData> postMedia(MultipartFile fileName) {
        // TODO
        return UploadApi.super.postMedia(fileName);
    }
}
