package com.delimce.aibroker.domain.dto.requests.users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserRegistration() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .passwordConfirmation("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidFirstName() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .passwordConfirmation("password123")
                .build();

        var orderedViolations = validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();
        assertEquals(2, orderedViolations.size());
        assertEquals("Name is required", orderedViolations.get(0));
        assertEquals("Name must be between 2 and 30 characters long", orderedViolations.get(1)); 
    }

    @Test
    void testInvalidLastName() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("")
                .email("john.doe@example.com")
                .password("password123")
                .passwordConfirmation("password123")
                .build();

        var orderedViolations = validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();
        
        assertEquals(2, orderedViolations.size());
        assertEquals("Last name is required", orderedViolations.get(0));
        assertEquals("Last name must be between 2 and 30 characters long", orderedViolations.get(1));
    }

    @Test
    void testInvalidEmail() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email")
                .password("password123")
                .passwordConfirmation("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testShortPassword() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("short")
                .passwordConfirmation("short")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void testLombokBuilderAndGetters() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .passwordConfirmation("password123")
                .build();

        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("john.doe@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("password123", request.getPasswordConfirmation());
    }
}