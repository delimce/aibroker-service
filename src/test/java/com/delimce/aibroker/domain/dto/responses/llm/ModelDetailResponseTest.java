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
    public void testModelDetailResponseSetters() {
        // Arrange
        ModelDetailResponse response = new ModelDetailResponse(null, null, null, false, null);

        String name = "claude-3";
        String provider = "Anthropic";
        ModelType type = ModelType.EMBEDDING;
        boolean enabled = true;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        response.setName(name);
        response.setProvider(provider);
        response.setType(type);
        response.setEnabled(enabled);
        response.setCreatedAt(createdAt);

        // Assert
        assertEquals(name, response.getName());
        assertEquals(provider, response.getProvider());
        assertEquals(type, response.getType());
        assertTrue(response.isEnabled());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        ModelDetailResponse response1 = new ModelDetailResponse("model1", "provider1", ModelType.CHAT, true, now);
        ModelDetailResponse response2 = new ModelDetailResponse("model1", "provider1", ModelType.CHAT, true, now);
        ModelDetailResponse response3 = new ModelDetailResponse("model2", "provider2", ModelType.EMBEDDING, false, now);

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        ModelDetailResponse response = new ModelDetailResponse("gpt-4", "OpenAI", ModelType.CHAT, true, now);

        // Act
        String toStringResult = response.toString();

        // Assert
        assertTrue(toStringResult.contains("gpt-4"));
        assertTrue(toStringResult.contains("OpenAI"));
        assertTrue(toStringResult.contains("CHAT"));
        assertTrue(toStringResult.contains("true"));
    }
}