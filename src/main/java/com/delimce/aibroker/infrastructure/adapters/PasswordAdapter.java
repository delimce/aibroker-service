package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.ports.PasswordInterface;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordAdapter implements PasswordInterface {

    private final PasswordEncoder passwordEncoder;

    public PasswordAdapter() {
        // Using BCrypt as the default password encoder
        this.passwordEncoder = new BCryptPasswordEncoder(12);
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
