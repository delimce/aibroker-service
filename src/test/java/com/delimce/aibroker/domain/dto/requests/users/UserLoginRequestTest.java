package com.delimce.aibroker.domain.dto.requests.users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.github.javafaker.Faker;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UserLoginRequestTest {

    private Validator validator;
    private static final Faker faker = new Faker();
    private static final String VALID_EMAIL = faker.internet().emailAddress();
    private static final String VALID_PASSWORD = faker.internet().password();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsAreValid_thenNoValidationViolations() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmailIsBlank_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("")
                .password(VALID_PASSWORD)
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsInvalid_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email("invalid-email")
                .password(VALID_PASSWORD)
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void whenPasswordIsBlank_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email(VALID_EMAIL)
                .password("")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message -> message.equals("Password is required")));
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .anyMatch(message -> message.equals("Password must be at least 8 characters long")));
    }

    @Test
    void whenPasswordIsTooShort_thenValidationViolation() {
        UserLoginRequest request = UserLoginRequest.builder()
                .email(VALID_EMAIL)
                .password("short")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 8 characters long", violations.iterator().next().getMessage());
    }
}