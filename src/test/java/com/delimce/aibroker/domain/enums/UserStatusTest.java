package com.delimce.aibroker.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserStatusTest {

    @Test
    void testEnumValues() {
        // Test that the enum has exactly the expected values
        UserStatus[] statuses = UserStatus.values();
        assertEquals(3, statuses.length);

        // Test that all expected values exist
        assertTrue(contains(statuses, UserStatus.ACTIVE));
        assertTrue(contains(statuses, UserStatus.INACTIVE));
        assertTrue(contains(statuses, UserStatus.PENDING));
    }

    @Test
    void testEnumValueOf() {
        // Test that valueOf returns the correct enum constant
        assertEquals(UserStatus.ACTIVE, UserStatus.valueOf("ACTIVE"));
        assertEquals(UserStatus.INACTIVE, UserStatus.valueOf("INACTIVE"));
        assertEquals(UserStatus.PENDING, UserStatus.valueOf("PENDING"));
    }

    @Test
    void testValueOfInvalidName() {
        // Test that valueOf throws IllegalArgumentException for invalid names
        assertThrows(IllegalArgumentException.class, () -> UserStatus.valueOf("DELETED"));
    }

    private boolean contains(UserStatus[] array, UserStatus value) {
        for (UserStatus status : array) {
            if (status == value) {
                return true;
            }
        }
        return false;
    }
}