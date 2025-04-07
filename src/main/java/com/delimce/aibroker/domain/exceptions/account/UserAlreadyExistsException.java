package com.delimce.aibroker.domain.exceptions.account;

import com.delimce.aibroker.domain.exceptions.DomainException;

public class UserAlreadyExistsException extends DomainException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "User with email %s already exists";

    public UserAlreadyExistsException(String email) {
        super(String.format(DEFAULT_MESSAGE, email), null);
    }

}
