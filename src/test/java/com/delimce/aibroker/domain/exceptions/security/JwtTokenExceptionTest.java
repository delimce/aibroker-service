package com.delimce.aibroker.domain.exceptions.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.delimce.aibroker.domain.exceptions.SecurityValidationException;

public class JwtTokenExceptionTest {

    @Test
    void shouldBeInstanceOfSecurityValidationException() {
        // Arrange & Act
        JwtTokenException exception = new JwtTokenException();
        
        // Assert
        assertTrue(exception instanceof SecurityValidationException);
    }
    
    @Test
    void shouldUseDefaultMessageWhenNoMessageProvided() {
        // Arrange & Act
        JwtTokenException exception = new JwtTokenException();
        
        // Assert
        assertEquals("Invalid or expired JWT token", exception.getMessage());
    }
    
    @Test
    void shouldUseProvidedMessage() {
        // Arrange
        String customMessage = "Custom error message";
        
        // Act
        JwtTokenException exception = new JwtTokenException(customMessage);
        
        // Assert
        assertEquals(customMessage, exception.getMessage());
    }
    
    @Test
    void shouldStoreProvidedCause() {
        // Arrange
        String customMessage = "Custom error message";
        Throwable cause = new RuntimeException("Original cause");
        
        // Act
        JwtTokenException exception = new JwtTokenException(customMessage, cause);
        
        // Assert
        assertEquals(customMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
