package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.web.servlet.error.ErrorController;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // You can add custom error handling logic here
        return "An error occurred. Please try again later.";
    }

}
