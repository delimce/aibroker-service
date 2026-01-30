package com.delimce.aibroker.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.delimce.aibroker.domain.dto.requests.users.UserLoginRequest;
import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.SecurityValidationException;
import com.delimce.aibroker.domain.exceptions.account.UserIsNotActiveException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

class AccountLoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenInterface jwtTokenAdapter;

    @InjectMocks
    private AccountLoginService accountLoginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("null")
    void shouldLoginSuccessfully() throws UserIsNotActiveException, SecurityValidationException {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.ACTIVE);
        user.setName("John");
        user.setLastName("Doe");

        UserToken userToken = new UserToken(
                "jwt-token",
                "test@test.com",
                System.currentTimeMillis() / 1000,
                (System.currentTimeMillis() / 1000) + 3600,
                3600000);

        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtTokenAdapter.generateUserToken(user)).thenReturn(userToken);

        // Act
        UserLoggedResponse response = accountLoginService.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("John", response.name());
        assertEquals("Doe", response.lastName());
        assertEquals("test@test.com", response.email());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountLoginService.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("test@test.com", "wrong-password");
        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> accountLoginService.execute(request));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotActive() {
        // Arrange
        UserLoginRequest request = new UserLoginRequest("test@test.com", "password");
        User user = new User();
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.INACTIVE);

        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // Act & Assert
        assertThrows(UserIsNotActiveException.class, () -> accountLoginService.execute(request));
    }
}