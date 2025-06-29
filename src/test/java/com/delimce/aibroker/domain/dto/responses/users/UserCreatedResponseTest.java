package com.delimce.aibroker.domain.dto.responses.users;

import org.junit.jupiter.api.Test;

import com.delimce.aibroker.utils.TestHandler;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserCreatedResponseTest extends TestHandler {

    @Test
    public void testRecordConstructorAndAccessors() {
        // Arrange
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        String token = faker().regexify("[a-zA-Z0-9]{20}");
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        UserCreatedResponse response = new UserCreatedResponse(name, lastName, email, token, createdAt);

        // Assert
        assertEquals(name, response.name());
        assertEquals(lastName, response.lastName());
        assertEquals(email, response.email());
        assertEquals(token, response.token());
        assertEquals(createdAt, response.createdAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        String token = faker().regexify("[a-zA-Z0-9]{20}");

        UserCreatedResponse response1 = new UserCreatedResponse(name, lastName, email, token, now);
        UserCreatedResponse response2 = new UserCreatedResponse(name, lastName, email, token, now);
        UserCreatedResponse response3 = new UserCreatedResponse(
                faker().name().firstName(),
                faker().name().lastName(),
                faker().internet().emailAddress(),
                faker().regexify("[a-zA-Z0-9]{20}"),
                now);

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
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        String token = faker().regexify("[a-zA-Z0-9]{20}");

        UserCreatedResponse response = new UserCreatedResponse(name, lastName, email, token, now);

        // Act
        String toStringResult = response.toString();

        // Assert
        assertTrue(toStringResult.contains(name));
        assertTrue(toStringResult.contains(lastName));
        assertTrue(toStringResult.contains(email));
        assertTrue(toStringResult.contains(token));
        assertTrue(toStringResult.contains("UserCreatedResponse"));
    }

    @Test
    public void testRecordEqualsWithDifferentName() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        String token = faker().regexify("[a-zA-Z0-9]{20}");

        UserCreatedResponse response1 = new UserCreatedResponse(faker().name().firstName(), lastName, email, token,
                now);
        UserCreatedResponse response2 = new UserCreatedResponse(faker().name().firstName(), lastName, email, token,
                now);

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    public void testRecordNotEqualsWithDifferentType() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        String token = faker().regexify("[a-zA-Z0-9]{20}");

        UserCreatedResponse response = new UserCreatedResponse(name, lastName, email, token, now);
        String differentType = faker().lorem().sentence();

        // Assert
        assertNotEquals(response, differentType);
    }

    @Test
    public void testRecordWithEmptyStrings() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        UserCreatedResponse response = new UserCreatedResponse("", "", "", "", now);

        // Assert
        assertEquals("", response.name());
        assertEquals("", response.lastName());
        assertEquals("", response.email());
        assertEquals("", response.token());
        assertEquals(now, response.createdAt());
    }

    @Test
    public void testRecordWithSpecialCharacters() {
        // Arrange
        String nameWithSpecialChars = faker().name().firstName() + " " + faker().regexify("[áéíóúñ]{3}");
        String lastNameWithSpecialChars = faker().name().lastName() + "-" + faker().regexify("[àèìòù]{3}");
        String emailWithSpecialChars = faker().internet().emailAddress();
        String tokenWithSpecialChars = faker().regexify("[a-zA-Z0-9!@#$%^&*()]{20}");
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        UserCreatedResponse response = new UserCreatedResponse(
                nameWithSpecialChars,
                lastNameWithSpecialChars,
                emailWithSpecialChars,
                tokenWithSpecialChars,
                createdAt);

        // Assert
        assertEquals(nameWithSpecialChars, response.name());
        assertEquals(lastNameWithSpecialChars, response.lastName());
        assertEquals(emailWithSpecialChars, response.email());
        assertEquals(tokenWithSpecialChars, response.token());
        assertEquals(createdAt, response.createdAt());
    }
}
