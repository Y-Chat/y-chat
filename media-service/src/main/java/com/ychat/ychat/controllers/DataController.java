package com.ychat.ychat.controllers;

import com.ychat.ychat.services.FirebaseStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
public class DataController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @GetMapping("/data/{*mapping}")
    public String getMedia(@PathVariable("mapping") String objectName) throws ResponseStatusException { // TODO wrap exceptions a little more precisely
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return firebaseStorageService.generateSignedUrl(objectName);
    }
}
