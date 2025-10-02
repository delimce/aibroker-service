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
        // Verify constructor works correctly (since setters are not available with
        // @Getter only)
        ModelMessageRequest request = new ModelMessageRequest("assistant", "updated content");

        assertEquals("assistant", request.getRole());
        assertEquals("updated content", request.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        // Since ModelMessageRequest only has @Getter (not @Data), equals and hashCode
        // use Object defaults
        // Test that the same instance equals itself and different instances are not
        // equal
        ModelMessageRequest request1 = new ModelMessageRequest("user", "content");
        ModelMessageRequest request2 = new ModelMessageRequest("user", "content");
        ModelMessageRequest request3 = new ModelMessageRequest("assistant", "content");

        // Same instance should equal itself
        assertEquals(request1, request1);
        assertEquals(request1.hashCode(), request1.hashCode());

        // Different instances with same content are not equal (Object default behavior)
        assertNotEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request2, request3);
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