package com.delimce.aibroker.application.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.delimce.aibroker.domain.dto.responses.users.UserLoggedResponse;
import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import com.delimce.aibroker.domain.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserRefreshServiceTest {

    @Mock
    private JwtTokenInterface jwtTokenInterface;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRefreshService userRefreshService;

    private User testUser;
    private UserToken userToken;
    private long tokenIssuedAt;

    @BeforeEach
    void setUp() {
        tokenIssuedAt = System.currentTimeMillis() / 1000;

        testUser = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        userToken = new UserToken(
                "new-jwt-token",
                "john@example.com",
                tokenIssuedAt,
                tokenIssuedAt + 86400,
                86400000);
    }

    @Test
    @SuppressWarnings("null")
    void execute_WithValidAuthenticatedUser_ShouldReturnUserLoggedResponse() throws JwtTokenException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtTokenInterface.generateUserToken(testUser)).thenReturn(userToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            UserLoggedResponse response = userRefreshService.execute();

            // Assert
            assertNotNull(response);
            assertEquals("new-jwt-token", response.token());
            assertEquals("John", response.name());
            assertEquals("Doe", response.lastName());
            assertEquals("john@example.com", response.email());

            // Verify that token timestamp was updated
            verify(userRepository, times(1)).save(any(User.class));
            verify(jwtTokenInterface, times(1)).generateUserToken(testUser);
        }
    }

    @SuppressWarnings("null")
    @Test
    void execute_ShouldUpdateUserTokenTimestamp() throws JwtTokenException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtTokenInterface.generateUserToken(testUser)).thenReturn(userToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            userRefreshService.execute();

            // Assert
            verify(userRepository).save(argThat(user -> user.getTokenTs() == tokenIssuedAt));
        }
    }

    @Test
    void execute_WithNullAuthentication_ShouldThrowIllegalArgumentException() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userRefreshService.execute());

            assertTrue(exception.getMessage().contains("Failed to get User from authentication"));
        }
    }

    @Test
    void execute_WithNonUserPrincipal_ShouldThrowIllegalArgumentException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        String nonUserPrincipal = "not-a-user-object";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(nonUserPrincipal);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userRefreshService.execute());

            assertTrue(exception.getMessage().contains("Failed to get User from authentication"));
        }
    }

    @Test
    @SuppressWarnings("null")
    void execute_WithJwtTokenException_ShouldPropagateException() throws JwtTokenException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtTokenInterface.generateUserToken(testUser)).thenThrow(new JwtTokenException("Token generation failed"));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            JwtTokenException exception = assertThrows(
                    JwtTokenException.class,
                    () -> userRefreshService.execute());

            assertTrue(exception.getMessage().contains("Token generation failed"));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    @SuppressWarnings("null")
    void execute_ShouldPreserveUserProperties() throws JwtTokenException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtTokenInterface.generateUserToken(testUser)).thenReturn(userToken);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            UserLoggedResponse response = userRefreshService.execute();

            // Assert - Verify response contains exact user properties
            assertEquals(testUser.getName(), response.name());
            assertEquals(testUser.getLastName(), response.lastName());
            assertEquals(testUser.getEmail(), response.email());
            assertNotNull(response.token());
        }
    }
}
