package com.delimce.aibroker.domain.dto.responses.users;

import org.junit.jupiter.api.Test;

import com.delimce.aibroker.utils.TestHandler;

import static org.junit.jupiter.api.Assertions.*;

public class UserLoggedResponseTest extends TestHandler {

    @Test
    public void testRecordConstructorAndAccessors() {
        // Arrange
        String token = faker().regexify("[a-zA-Z0-9]{20}");
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();

        // Act
        UserLoggedResponse response = new UserLoggedResponse(token, name, lastName, email);

        // Assert
        assertEquals(token, response.token());
        assertEquals(name, response.name());
        assertEquals(lastName, response.lastName());
        assertEquals(email, response.email());
    }

    @Test
    public void testRecordWithNullValues() {
        // Arrange & Act
        UserLoggedResponse response = new UserLoggedResponse(null, null, null, null);

        // Assert
        assertNull(response.token());
        assertNull(response.name());
        assertNull(response.lastName());
        assertNull(response.email());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        String token = faker().regexify("[a-zA-Z0-9]{20}");
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();

        UserLoggedResponse response1 = new UserLoggedResponse(token, name, lastName, email);
        UserLoggedResponse response2 = new UserLoggedResponse(token, name, lastName, email);
        UserLoggedResponse response3 = new UserLoggedResponse(
                faker().regexify("[a-zA-Z0-9]{20}"),
                faker().name().firstName(),
                faker().name().lastName(),
                faker().internet().emailAddress());

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        String token = faker().regexify("[a-zA-Z0-9]{20}");
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();

        UserLoggedResponse response = new UserLoggedResponse(token, name, lastName, email);

        // Act
        String toStringResult = response.toString();

        // Assert
        assertTrue(toStringResult.contains(token));
        assertTrue(toStringResult.contains(name));
        assertTrue(toStringResult.contains(lastName));
        assertTrue(toStringResult.contains(email));
        assertTrue(toStringResult.contains("UserLoggedResponse"));
    }

    @Test
    public void testRecordWithEmptyStrings() {
        // Act
        UserLoggedResponse response = new UserLoggedResponse("", "", "", "");

        // Assert
        assertEquals("", response.token());
        assertEquals("", response.name());
        assertEquals("", response.lastName());
        assertEquals("", response.email());
    }
}
