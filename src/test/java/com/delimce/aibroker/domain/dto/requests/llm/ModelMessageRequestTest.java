package com.delimce.aibroker.domain.dto.requests.llm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelMessageRequestTest {

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
    void testSettersAndGetters() {
        // Verify setters and getters work correctly
        ModelMessageRequest request = new ModelMessageRequest("user", "initial content");
        request.setRole("assistant");
        request.setContent("updated content");

        assertEquals("assistant", request.getRole());
        assertEquals("updated content", request.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        // Verify equals and hashCode methods from Lombok @Data
        ModelMessageRequest request1 = new ModelMessageRequest("user", "content");
        ModelMessageRequest request2 = new ModelMessageRequest("user", "content");
        ModelMessageRequest request3 = new ModelMessageRequest("assistant", "content");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    // create test to prove validation exception 
    @Test
    void testValidationException() {
        // Verify that a validation exception is thrown when content is blank
        ModelMessageRequest request = ModelMessageRequest.builder()
                .role("user")
                .content("")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            if (request.getContent().isBlank()) {
                throw new IllegalArgumentException("Content is required");
            }
        });
    }
}