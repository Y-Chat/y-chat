package com.ychat.ychat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    // used by gcp to check if the api-gateway is healthy before exposing it to the internet
    @GetMapping("/")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("gateway healthy");
    }
}
