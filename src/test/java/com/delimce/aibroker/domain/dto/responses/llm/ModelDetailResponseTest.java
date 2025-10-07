package com.delimce.aibroker.domain.dto.responses.llm;

import com.delimce.aibroker.domain.enums.ModelType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ModelDetailResponseTest {

    @Test
    public void testModelDetailResponseConstructorAndGetters() {
        // Arrange
        String name = "gpt-4";
        String provider = "OpenAI";
        ModelType type = ModelType.CHAT;
        boolean enabled = true;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        ModelDetailResponse response = new ModelDetailResponse(name, provider, type, enabled, createdAt);

        // Assert
        assertEquals(name, response.getName());
        assertEquals(provider, response.getProvider());
        assertEquals(type, response.getType());
        assertTrue(response.isEnabled());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    public void testModelDetailResponseConstructor() {
        // Since ModelDetailResponse only has @Getter (no setters), test constructor
        // instead
        String name = "claude-3";
        String provider = "Anthropic";
        ModelType type = ModelType.EMBEDDING;
        boolean enabled = true;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act - Use constructor instead of setters
        ModelDetailResponse response = new ModelDetailResponse(name, provider, type, enabled, createdAt);

        // Assert
        assertEquals(name, response.getName());
        assertEquals(provider, response.getProvider());
        assertEquals(type, response.getType());
        assertTrue(response.isEnabled());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Since ModelDetailResponse only has @Getter (not @Data), equals and hashCode
        // use Object defaults
        LocalDateTime now = LocalDateTime.now();
        ModelDetailResponse response1 = new ModelDetailResponse("model1", "provider1", ModelType.CHAT, true, now);
        ModelDetailResponse response2 = new ModelDetailResponse("model1", "provider1", ModelType.CHAT, true, now);
        ModelDetailResponse response3 = new ModelDetailResponse("model2", "provider2", ModelType.EMBEDDING, false, now);

        // Same instance should equal itself
        assertEquals(response1, response1);
        assertEquals(response1.hashCode(), response1.hashCode());

        // Different instances are not equal (Object default behavior)
        assertNotEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response2, response3);
    }

    @Test
    public void testToString() {
        // Since ModelDetailResponse only has @Getter (not @Data), toString uses Object
        // default
        LocalDateTime now = LocalDateTime.now();
        ModelDetailResponse response = new ModelDetailResponse("gpt-4", "OpenAI", ModelType.CHAT, true, now);

        // Act
        String toStringResult = response.toString();

        // Assert - Object default toString contains class name and hash code
        assertTrue(toStringResult.contains("ModelDetailResponse"));
        assertTrue(toStringResult.contains("@"));
        // Object toString format: com.package.ClassName@hashcode
        assertTrue(toStringResult.matches(".*ModelDetailResponse@[a-f0-9]+"));
    }
}