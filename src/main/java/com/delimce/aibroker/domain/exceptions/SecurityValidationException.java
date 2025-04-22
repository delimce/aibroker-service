package com.delimce.aibroker.domain.exceptions;

public abstract class SecurityValidationException extends Throwable {

    public SecurityValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
