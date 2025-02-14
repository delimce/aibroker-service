package com.delimce.shared.domain.exceptions;

public abstract class DomainException extends Throwable {

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
