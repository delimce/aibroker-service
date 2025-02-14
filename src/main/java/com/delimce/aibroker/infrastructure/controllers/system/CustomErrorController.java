package com.delimce.aibroker.infrastructure.controllers.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        try {
            int statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return "404 - Resource not found.";
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return "An error occurred. Please try again later.";
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "404 - Resource not found.";
        }
    }

}
