package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.ports.PasswordInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordAdapter implements PasswordInterface {

    private final PasswordEncoder passwordEncoder;

    public PasswordAdapter() {
        // Using BCrypt as the default password encoder
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Autowired
    public PasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}