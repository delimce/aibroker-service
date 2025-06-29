package com.delimce.aibroker.domain.dto.responses.users;

import org.junit.jupiter.api.Test;

import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.utils.TestHandler;

import static org.junit.jupiter.api.Assertions.*;

public class UserListResponseTest extends TestHandler {

    @Test
    public void testRecordConstructorAndAccessors() {
        // Arrange
        Long id = faker().number().randomNumber();
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        UserStatus status = UserStatus.ACTIVE;

        // Act
        UserListResponse response = new UserListResponse(id, name, lastName, email, status);

        // Assert
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(lastName, response.lastName());
        assertEquals(email, response.email());
        assertEquals(status, response.status());
    }

    @Test
    public void testRecordWithNullValues() {
        // Arrange & Act
        UserListResponse response = new UserListResponse(null, null, null, null, null);

        // Assert
        assertNull(response.id());
        assertNull(response.name());
        assertNull(response.lastName());
        assertNull(response.email());
        assertNull(response.status());
    }

    @Test
    public void testToString() {
        // Arrange
        Long id = faker().number().randomNumber();
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        UserStatus status = UserStatus.ACTIVE;

        UserListResponse response = new UserListResponse(id, name, lastName, email, status);

        // Act
        String toStringResult = response.toString();

        // Assert
        assertTrue(toStringResult.contains(id.toString()));
        assertTrue(toStringResult.contains(name));
        assertTrue(toStringResult.contains(lastName));
        assertTrue(toStringResult.contains(email));
        assertTrue(toStringResult.contains(status.toString()));
        assertTrue(toStringResult.contains("UserListResponse"));
    }

    @Test
    public void testRecordEqualsWithDifferentId() {
        // Arrange
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        UserStatus status = UserStatus.ACTIVE;

        UserListResponse response1 = new UserListResponse(faker().number().randomNumber(), name, lastName, email,
                status);
        UserListResponse response2 = new UserListResponse(faker().number().randomNumber(), name, lastName, email,
                status);

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    public void testRecordEqualsWithDifferentName() {
        // Arrange
        Long id = faker().number().randomNumber();
        String lastName = faker().name().lastName();
        String email = faker().internet().emailAddress();
        UserStatus status = UserStatus.ACTIVE;

        UserListResponse response1 = new UserListResponse(id, faker().name().firstName(), lastName, email, status);
        UserListResponse response2 = new UserListResponse(id, faker().name().firstName(), lastName, email, status);

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    public void testRecordEqualsWithDifferentLastName() {
        // Arrange
        Long id = faker().number().randomNumber();
        String name = faker().name().firstName();
        String email = faker().internet().emailAddress();
        UserStatus status = UserStatus.ACTIVE;

        UserListResponse response1 = new UserListResponse(id, name, faker().name().lastName(), email, status);
        UserListResponse response2 = new UserListResponse(id, name, faker().name().lastName(), email, status);

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    public void testRecordEqualsWithDifferentEmail() {
        // Arrange
        Long id = faker().number().randomNumber();
        String name = faker().name().firstName();
        String lastName = faker().name().lastName();
        UserStatus status = UserStatus.ACTIVE;

        UserListResponse response1 = new UserListResponse(id, name, lastName, faker().internet().emailAddress(),
                status);
        UserListResponse response2 = new UserListResponse(id, name, lastName, faker().internet().emailAddress(),
                status);

        // Assert
        assertNotEquals(response1, response2);
    }

}
