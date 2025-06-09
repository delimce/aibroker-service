package com.delimce.aibroker.domain.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SecurityValidationExceptionTest {

    // Concrete implementation of SecurityValidationException for testing
    private static class TestSecurityValidationException extends SecurityValidationException {
        public TestSecurityValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Test
    void shouldStoreMessageAndCause() {
        // Arrange
        String errorMessage = "Security validation failed";
        Exception cause = new RuntimeException("Root cause");
        
        // Act
        SecurityValidationException exception = new TestSecurityValidationException(errorMessage, cause);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void shouldExtendThrowable() {
        // Arrange & Act
        SecurityValidationException exception = new TestSecurityValidationException("test", null);
        
        // Assert
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    void shouldAllowNullCause() {
        // Arrange
        String errorMessage = "Security error";
        
        // Act
        SecurityValidationException exception = new TestSecurityValidationException(errorMessage, null);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
}
