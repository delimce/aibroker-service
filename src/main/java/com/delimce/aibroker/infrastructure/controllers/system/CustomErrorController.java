package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleError(HttpServletRequest request) {

        int statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "404 - Resource not found.";
        }
        return "An error occurred. Please try again later.";
    }

}
