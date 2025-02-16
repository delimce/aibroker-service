package com.delimce.aibroker.domain.exceptions;

public abstract class DomainException extends Throwable {

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
