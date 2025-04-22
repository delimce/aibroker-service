package com.delimce.aibroker.domain.exceptions.security;

import com.delimce.aibroker.domain.exceptions.SecurityValidationException;

public class JwtTokenException extends SecurityValidationException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Invalid or expired JWT token";

    public JwtTokenException() {
        super(DEFAULT_MESSAGE, null);
    }

    public JwtTokenException(String message) {
        super(message, null);
    }

    public JwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}