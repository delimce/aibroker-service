package com.delimce.aibroker.infrastructure.adapters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class PasswordAdapterTest {

    private PasswordAdapter passwordAdapter;

    @BeforeEach
    void setUp() {
        passwordAdapter = new PasswordAdapter();
    }

    @Test
    void encode_ShouldReturnEncodedPassword() {
        // given
        String rawPassword = "password123";

        // when
        String encodedPassword = passwordAdapter.encode(rawPassword);

        // then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    void matches_WithCorrectPassword_ShouldReturnTrue() {
        // given
        String rawPassword = "securePassword";
        String encodedPassword = passwordAdapter.encode(rawPassword);

        // when
        boolean result = passwordAdapter.matches(rawPassword, encodedPassword);

        // then
        assertTrue(result);
    }

    @Test
    void matches_WithIncorrectPassword_ShouldReturnFalse() {
        // given
        String rawPassword = "securePassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordAdapter.encode(rawPassword);

        // when
        boolean result = passwordAdapter.matches(wrongPassword, encodedPassword);

        // then
        assertFalse(result);
    }
}