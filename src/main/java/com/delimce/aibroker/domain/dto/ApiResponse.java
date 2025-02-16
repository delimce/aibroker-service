package com.delimce.aibroker.domain.dto;

import org.springframework.http.HttpStatus;

public record ApiResponse(Object info, int status, String message) {


    public static final String CREATED = HttpStatus.CREATED.getReasonPhrase();

    public ApiResponse {
        if (status < 100 || status > 599) {
            throw new IllegalArgumentException("Invalid status code: " + status);
        }
    }

    public ApiResponse(Object info) {
        this(info, 200, "OK");
    }

    public ApiResponse(String message, int status) {
        this(null, status, message);
    }
}
