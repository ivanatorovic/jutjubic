package com.example.jutjubic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoAmIController {
    @GetMapping("/api/whoami")
    public String whoami() {
        return "Hello from " + System.getenv().getOrDefault("INSTANCE_ID", "unknown");
    }
}
