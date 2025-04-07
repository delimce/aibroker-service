package com.delimce.aibroker.domain.exceptions.account;

import com.delimce.aibroker.domain.exceptions.DomainException;

public class UserIsNotActiveException extends DomainException {
    
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "User is not active";

    public UserIsNotActiveException() {
        super(DEFAULT_MESSAGE, null);
    }
}
