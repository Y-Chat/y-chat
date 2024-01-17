package com.ychat.ychat.controllers;

import com.ychat.ychat.services.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URL;

@Controller
@RequestMapping("/")
public class DataController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @GetMapping("/{objectName}")
    public String getMedia(@PathVariable String objectName) {
        return firebaseStorageService.generateSignedUrl(objectName);
    }
}
