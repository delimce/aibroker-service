package com.delimce.shared.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ApiResponseTest {

    @Test
    public void testApiResponseWithAllParameters() {
        ApiResponse response = new ApiResponse("Info", 200, "OK");
        assertEquals("Info", response.info());
        assertEquals(200, response.status());
        assertEquals("OK", response.message());
    }

    @Test
    public void testApiResponseWithInfoOnly() {
        ApiResponse response = new ApiResponse("Info");
        assertEquals("Info", response.info());
        assertEquals(200, response.status());
        assertEquals("OK", response.message());
    }

    @Test
    public void testApiResponseWithMessageAndStatus() {
        ApiResponse response = new ApiResponse("Error", 404);
        assertNull(response.info());
        assertEquals(404, response.status());
        assertEquals("Error", response.message());
    }

    @Test
    public void testInvalidStatusCodeThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ApiResponse("Info", 99, "Invalid");
        });
        assertEquals("Invalid status code: 99", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            new ApiResponse("Info", 600, "Invalid");
        });
        assertEquals("Invalid status code: 600", exception.getMessage());
    }
}