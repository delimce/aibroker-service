package com.delimce.aibroker.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTypeTest {

    @Test
    public void testEnumValues() {
        // Test that the enum has exactly the expected values
        assertEquals(2, ModelType.values().length);
        
        // Test that the enum contains the expected values
        assertTrue(containsValue(ModelType.values(), ModelType.CHAT));
        assertTrue(containsValue(ModelType.values(), ModelType.EMBEDDING));
    }
    
    @Test
    public void testEnumValuesOrder() {
        // Test that the values are in the expected order
        ModelType[] values = ModelType.values();
        assertEquals(ModelType.CHAT, values[0]);
        assertEquals(ModelType.EMBEDDING, values[1]);
    }
    
    @Test
    public void testValueOf() {
        // Test valueOf method works correctly
        assertEquals(ModelType.CHAT, ModelType.valueOf("CHAT"));
        assertEquals(ModelType.EMBEDDING, ModelType.valueOf("EMBEDDING"));
    }
    
    @Test
    public void testValueOfWithInvalidValue() {
        // Test that valueOf throws IllegalArgumentException for invalid values
        assertThrows(IllegalArgumentException.class, () -> ModelType.valueOf("INVALID_VALUE"));
    }
    
    @Test
    public void testToString() {
        // Test toString returns the correct string representation
        assertEquals("CHAT", ModelType.CHAT.toString());
        assertEquals("EMBEDDING", ModelType.EMBEDDING.toString());
    }
    
    // Helper method to check if an enum value is in an array
    private boolean containsValue(ModelType[] values, ModelType valueToFind) {
        for (ModelType value : values) {
            if (value == valueToFind) {
                return true;
            }
        }
        return false;
    }
}
