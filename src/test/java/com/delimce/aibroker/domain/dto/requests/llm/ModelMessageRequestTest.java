package com.delimce.aibroker.domain.dto.requests.llm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ModelMessageRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultRoleValue() {
        // Verify that the default role is "user"
        ModelMessageRequest request = ModelMessageRequest.builder()
                .content("test content")
                .build();
        assertEquals("user", request.getRole());
    }

    @Test
    void testCustomRoleValue() {
        // Verify that a custom role can be set
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("assistant")
                .content("test content")
                .build();
        assertEquals("assistant", request.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        // Verify the AllArgsConstructor works
        ModelMessageRequest request = new ModelMessageRequest("system", "test instruction");
        assertEquals("system", request.getRole());
        assertEquals("test instruction", request.getContent());
    }

    @Test
    void testValidMessageRequest() {
        // Test a valid message request
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content("Hello, how are you?")
                .build();

        Set<ConstraintViolation<ModelMessageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "No violations expected for valid request");
    }

    @Test
    void testBlankContentValidation() {
        // Test that blank content is invalid
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content("")
                .build();

        Set<ConstraintViolation<ModelMessageRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Violations expected for blank content");

        String errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(errorMessages.contains("Content is required"),
                "Should contain 'Content is required' error");
    }

    @Test
    void testContentLengthExactly800Characters() {
        // Verify that content with exactly 800 characters is valid
        String content800 = "a".repeat(800);
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content(content800)
                .build();

        Set<ConstraintViolation<ModelMessageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(),
                "Content with exactly 800 characters should be valid");

        assertEquals(content800, request.getContent());
        assertEquals(800, request.getContent().length());
    }

    @Test
    void testContentLengthLessThan800Characters() {
        // Verify that content with less than 800 characters is valid
        String content799 = "a".repeat(799);
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content(content799)
                .build();

        Set<ConstraintViolation<ModelMessageRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(),
                "Content with 799 characters should be valid");

        assertEquals(content799, request.getContent());
        assertEquals(799, request.getContent().length());
    }

    @Test
    void testContentBoundaryConditions() {
        // Test boundary conditions around 800 characters
        String content799 = "x".repeat(799);
        String content800 = "x".repeat(800);
        String content801 = "x".repeat(801);

        ModelMessageRequest request799 = ModelMessageRequest.builder()
                .content(content799)
                .build();
        ModelMessageRequest request800 = ModelMessageRequest.builder()
                .content(content800)
                .build();
        ModelMessageRequest request801 = ModelMessageRequest.builder()
                .content(content801)
                .build();

        // Test validation results
        Set<ConstraintViolation<ModelMessageRequest>> violations799 = validator.validate(request799);
        Set<ConstraintViolation<ModelMessageRequest>> violations800 = validator.validate(request800);
        Set<ConstraintViolation<ModelMessageRequest>> violations801 = validator.validate(request801);

        assertTrue(violations799.isEmpty(), "799 characters should be valid");
        assertTrue(violations800.isEmpty(), "800 characters should be valid");
        assertFalse(violations801.isEmpty(), "801 characters should be invalid");

        // Verify lengths
        assertEquals(799, request799.getContent().length());
        assertEquals(800, request800.getContent().length());
        assertEquals(801, request801.getContent().length());
    }

    @Test
    void testMultipleValidationErrors() {
        // Test that multiple validation errors can occur
        String content801 = "a".repeat(801);
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content(content801)
                .build();

        Set<ConstraintViolation<ModelMessageRequest>> violations = validator.validate(request);
        // Should have at least the size violation
        assertFalse(violations.isEmpty());

        // Check that we get the size violation message
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Content must be less than 800 characters"));
        assertTrue(hasSizeViolation, "Should have size violation for 801 characters");
    }
}