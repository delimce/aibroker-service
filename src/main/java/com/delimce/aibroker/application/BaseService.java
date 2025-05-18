package com.delimce.aibroker.application;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.delimce.aibroker.domain.entities.User;

public abstract class BaseService {

    protected User fetchAuthenticatedUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        try {
            return (User) auth.getPrincipal();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get User from authentication: " + e.getMessage());
        }
    }

}
