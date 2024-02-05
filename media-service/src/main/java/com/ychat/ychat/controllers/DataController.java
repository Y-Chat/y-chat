package com.ychat.ychat.controllers;

import com.openapi.gen.media.api.DataApi;
import com.openapi.gen.media.dto.GetMedia200Response;
import com.ychat.ychat.FirebaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataController implements DataApi {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseInitializer.class);

    @Autowired
    private FirebaseInitializer firebaseInitializer;

    @Override
    public ResponseEntity<GetMedia200Response> getMedia(String objectName) {
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }

        String url = firebaseInitializer.generateSignedUrl(objectName);
        return ResponseEntity.ok(new GetMedia200Response(url));
    }
}
