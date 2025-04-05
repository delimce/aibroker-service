package com.delimce.aibroker.domain.dto.requests.users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.delimce.aibroker.utils.TestHandler;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserLoginRequestTest extends TestHandler {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsAreValid_thenNoValidationViolations() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email(faker().internet().emailAddress())
                .password(faker().internet().password())
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmailIsBlank_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("")
                .password(faker().internet().password())
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsInvalid_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("invalid-email")
                .password(faker().internet().password())
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void whenPasswordIsBlank_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email(faker().internet().emailAddress())
                .password("")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void whenPasswordIsTooShort_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
        .email(faker().internet().emailAddress())
                .password("short")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}