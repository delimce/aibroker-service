package com.delimce.aibroker.domain.dto.requests.llm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModelRequestTest {

    private Validator validator;
    private static final String VALID_MODEL = "gpt-4-turbo-preview";

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenModelIsTooShort_thenValidationViolation() {
        ModelRequest request = ModelRequest.builder()
                .model("short")
                .messages(new ModelMessageRequest[] {
                        new ModelMessageRequest("user", "Hello")
                })
                .build();

        Set<ConstraintViolation<ModelRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Model must be at least 12 character long", violations.iterator().next().getMessage());
    }

    @Test
    void testDefaultStreamValue() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .build();

        assertFalse(request.isStream());
    }

    @Test
    void testCustomStreamValue() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .stream(true)
                .build();

        assertTrue(request.isStream());
    }

    @Test
    void testDefaultTemperatureValue() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .build();

        assertEquals(1, request.getTemperature());
    }

    @Test
    void testCustomTemperatureValue() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .temperature(0)
                .build();

        assertEquals(0, request.getTemperature());
    }

    @Test
    void testAllArgsConstructor() {
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello")
        };

        ModelRequest request = new ModelRequest(VALID_MODEL, true, messages, 0);

        assertEquals(VALID_MODEL, request.getModel());
        assertTrue(request.isStream());
        assertArrayEquals(messages, request.getMessages());
        assertEquals(0, request.getTemperature());
    }

    @Test
    void testNoArgsConstructor() {
        ModelRequest request = new ModelRequest();

        assertNull(request.getModel());
        assertFalse(request.isStream());
        assertNull(request.getMessages());
        assertEquals(1, request.getTemperature());
    }

    @Test
    void testSettersAndGetters() {
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello")
        };

        ModelRequest request = new ModelRequest();
        request.setModel(VALID_MODEL);
        request.setStream(true);
        request.setMessages(messages);
        request.setTemperature(0);

        assertEquals(VALID_MODEL, request.getModel());
        assertTrue(request.isStream());
        assertArrayEquals(messages, request.getMessages());
        assertEquals(0, request.getTemperature());
    }

    @Test
    void testEqualsAndHashCode() {
        ModelMessageRequest[] messages = new ModelMessageRequest[] {
                new ModelMessageRequest("user", "Hello")
        };

        ModelRequest request1 = new ModelRequest(VALID_MODEL, true, messages, 0);
        ModelRequest request2 = new ModelRequest(VALID_MODEL, true, messages, 0);
        ModelRequest request3 = new ModelRequest("different-model-name", true, messages, 0);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void whenAllFieldsAreValid_thenNoValidationViolations() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .messages(new ModelMessageRequest[] {
                        new ModelMessageRequest("user", "Hello, how are you?")
                })
                .build();

        Set<ConstraintViolation<ModelRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenMessagesIsEmpty_thenValidationViolation() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .messages(new ModelMessageRequest[] {})
                .build();

        Set<ConstraintViolation<ModelRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Messages array cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void whenMessageContentIsBlank_thenValidationViolation() {
        ModelRequest request = ModelRequest.builder()
                .model(VALID_MODEL)
                .messages(new ModelMessageRequest[] {
                        new ModelMessageRequest("user", "")
                })
                .build();

        Set<ConstraintViolation<ModelRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());

        // Check that the message content validation is triggered via cascading
        // validation
        String violationPath = violations.iterator().next().getPropertyPath().toString();
        assertTrue(violationPath.contains("messages"));
        assertEquals("Content is required", violations.iterator().next().getMessage());
    }
}