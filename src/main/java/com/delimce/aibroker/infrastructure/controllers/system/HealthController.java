package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String status() {
        return "OK";
    }

}
