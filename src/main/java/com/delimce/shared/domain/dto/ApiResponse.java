package com.delimce.shared.domain.dto;

public record ApiResponse(Object info, int status, String message) {
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
